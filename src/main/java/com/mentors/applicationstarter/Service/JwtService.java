package com.mentors.applicationstarter.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface JwtService {
    /**
     * Extract username (email) from JWT token
     */
    String extractUsername(String token);

    /**
     * Generate access token (short-lived)
     */
    String generateToken(UserDetails userDetails);

    /**
     * Generate refresh token (long-lived)
     */
    String generateRefreshToken(UserDetails userDetails);

    /**
     * Validate JWT token against user details
     */
    boolean isTokenValid(String token, UserDetails userDetails);

    /**
     * Check if token is expired
     */
    boolean isTokenExpired(String token);
}
