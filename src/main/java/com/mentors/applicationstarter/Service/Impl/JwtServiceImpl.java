package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Configuration.JwtProperties;
import com.mentors.applicationstarter.Constant.SecurityConstant;
import com.mentors.applicationstarter.Service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.mentors.applicationstarter.Constant.SecurityConstant.JWT_TOKEN_SECRET;


@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtServiceImpl.class);

    // Inject JwtProperties to get configuration values
    private final JwtProperties jwtProperties;

    /**
     * Extract username (email) from JWT token
     */
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract a specific claim from the token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Generate access token (short-lived - 15 minutes)
     * This token is used for API authentication
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();

        // You can add custom claims here if needed
        // For example: role, permissions, etc.
        // extraClaims.put("role", userDetails.getAuthorities());

        return generateToken(extraClaims, userDetails, jwtProperties.getAccessTokenExpiration());
    }

    /**
     * Generate refresh token (long-lived - 7 days)
     * This token is used to get a new access token when it expires
     */
    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();

        // Refresh tokens typically have minimal claims
        // Just enough to identify the user and issue a new access token

        return generateToken(extraClaims, userDetails, jwtProperties.getRefreshTokenExpiration());
    }

    /**
     * Generate JWT token with custom claims and expiration
     * @param extraClaims Additional claims to include in the token
     * @param userDetails User information
     * @param expiration Token expiration time in milliseconds
     */
    private String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        long issuedAt = System.currentTimeMillis();
        long expiresAt = issuedAt + expiration;

        LOGGER.debug("Generating token for user: {} with expiration: {} ms",
                userDetails.getUsername(), expiration);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername()) // Usually the email
                .issuedAt(new Date(issuedAt))
                .expiration(new Date(expiresAt))
                .signWith(getSigningKey()) // Sign with secret key
                .compact();
    }

    /**
     * Validate JWT token against user details
     * Checks if the token belongs to the user and is not expired
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);

            if (!isValid) {
                LOGGER.warn("Token validation failed for user: {}", username);
            }

            return isValid;
        } catch (Exception e) {
            LOGGER.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token is expired
     */
    @Override
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            LOGGER.error("Error checking token expiration: {}", e.getMessage());
            return true; // Consider expired if there's an error
        }
    }

    /**
     * Extract expiration date from token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Get the signing key for JWT
     * Uses the secret from JwtProperties configuration
     */
    private SecretKey getSigningKey() {
        // Decode the Base64 encoded secret key
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
