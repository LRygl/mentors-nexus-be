package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Configuration.JwtProperties;
import com.mentors.applicationstarter.Service.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CookieServiceImpl implements CookieService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CookieServiceImpl.class);

    private final JwtProperties jwtProperties;

    /**
     * Add access token as HttpOnly cookie
     */
    @Override
    public void addAccessTokenCookie(HttpServletResponse response, String token) {
        int maxAge = (int) (jwtProperties.getAccessTokenExpiration() / 1000);

        // ✅ CALLED HERE - Creates the ResponseCookie
        ResponseCookie cookie = createSecureResponseCookie(
                jwtProperties.getCookie().getAccessTokenName(),
                token,
                maxAge
        );

        // Then adds it to response
        response.addHeader("Set-Cookie", cookie.toString());

        LOGGER.info("Access token cookie added: secure={}, sameSite={}",
                jwtProperties.getCookie().getSecure(),
                jwtProperties.getCookie().getSameSite());
    }

    /**
     * Add refresh token as HttpOnly cookie
     */
    @Override
    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        int maxAge = (int) (jwtProperties.getRefreshTokenExpiration() / 1000);

        // ✅ CALLED HERE TOO - Creates the ResponseCookie
        ResponseCookie cookie = createSecureResponseCookie(
                jwtProperties.getCookie().getRefreshTokenName(),
                token,
                maxAge
        );

        response.addHeader("Set-Cookie", cookie.toString());

        LOGGER.info("Refresh token cookie added: secure={}, sameSite={}",
                jwtProperties.getCookie().getSecure(),
                jwtProperties.getCookie().getSameSite());
    }


    private ResponseCookie createSecureResponseCookie(String name, String value, int maxAge) {
        // Spring's fluent builder API
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(jwtProperties.getCookie().getSecure())
                .path(jwtProperties.getCookie().getPath())
                .maxAge(maxAge)
                .sameSite(jwtProperties.getCookie().getSameSite());  // ✅ Works reliably

        // Set domain if configured
        String domain = jwtProperties.getCookie().getDomain();
        if (domain != null && !domain.isEmpty()) {
            builder.domain(domain);
        }

        return builder.build();
    }

    // =========================================================================
    // READING COOKIES - Still uses Cookie (this part works fine)
    // =========================================================================

    /**
     * Extract access token from cookie
     */
    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return extractTokenFromCookie(request, jwtProperties.getCookie().getAccessTokenName());
    }

    /**
     * Extract refresh token from cookie
     */
    @Override
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return extractTokenFromCookie(request, jwtProperties.getCookie().getRefreshTokenName());
    }

    /**
     * Reading cookies from request works fine with Cookie class
     * The issue is only when WRITING cookies to response
     */
    private Optional<String> extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            LOGGER.debug("No cookies in request");
            return Optional.empty();
        }

        Optional<String> token = Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();

        if (token.isEmpty()) {
            LOGGER.debug("Cookie '{}' not found. Available: {}",
                    cookieName,
                    Arrays.stream(request.getCookies())
                            .map(Cookie::getName)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("none")
            );
        }

        return token;
    }

    // =========================================================================
    // DELETING COOKIES - Also needs ResponseCookie
    // =========================================================================

    @Override
    public void deleteAccessTokenCookie(HttpServletResponse response) {
        deleteResponseCookie(response, jwtProperties.getCookie().getAccessTokenName());
    }

    @Override
    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        deleteResponseCookie(response, jwtProperties.getCookie().getRefreshTokenName());
    }

    @Override
    public void deleteAllAuthCookies(HttpServletResponse response) {
        deleteAccessTokenCookie(response);
        deleteRefreshTokenCookie(response);
        LOGGER.debug("All authentication cookies deleted");
    }

    /**
     * Helper method to delete a cookie
     */
    private void deleteResponseCookie(HttpServletResponse response, String cookieName) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(jwtProperties.getCookie().getSecure())
                .path(jwtProperties.getCookie().getPath())
                .maxAge(0)  // Delete immediately
                .sameSite(jwtProperties.getCookie().getSameSite());  // ✅ Must match

        String domain = jwtProperties.getCookie().getDomain();
        if (domain != null && !domain.isEmpty()) {
            builder.domain(domain);
        }

        response.addHeader("Set-Cookie", builder.build().toString());
        LOGGER.debug("Cookie '{}' deleted", cookieName);
    }
}
