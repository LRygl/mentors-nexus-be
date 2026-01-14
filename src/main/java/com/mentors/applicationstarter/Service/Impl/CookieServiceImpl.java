package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Configuration.JwtProperties;
import com.mentors.applicationstarter.Service.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
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
        int maxAge = (int) (jwtProperties.getAccessTokenExpiration() / 1000); // Convert to seconds
        Cookie cookie = createSecureCookie(
                jwtProperties.getCookie().getAccessTokenName(),
                token,
                maxAge
        );
        response.addCookie(cookie);
        LOGGER.debug("Access token cookie added with maxAge: {} seconds", maxAge);
    }

    /**
     * Add refresh token as HttpOnly cookie
     */
    @Override
    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        int maxAge = (int) (jwtProperties.getRefreshTokenExpiration() / 1000); // Convert to seconds
        Cookie cookie = createSecureCookie(
                jwtProperties.getCookie().getRefreshTokenName(),
                token,
                maxAge
        );
        response.addCookie(cookie);
        LOGGER.debug("Refresh token cookie added with maxAge: {} seconds", maxAge);
    }

    /**
     * Create a secure cookie with standard security settings
     */
    private Cookie createSecureCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);  // Prevents JavaScript access (XSS protection)
        cookie.setSecure(jwtProperties.getCookie().getSecure()); // HTTPS only in production
        cookie.setPath(jwtProperties.getCookie().getPath());
        cookie.setMaxAge(maxAge);

        // Set domain if configured
        if (jwtProperties.getCookie().getDomain() != null) {
            cookie.setDomain(jwtProperties.getCookie().getDomain());
        }

        // Set SameSite attribute (Spring Boot 3.2+)
        cookie.setAttribute("SameSite", jwtProperties.getCookie().getSameSite());

        return cookie;
    }

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
     * Helper method to extract token from cookie by name
     */
    private Optional<String> extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    /**
     * Delete access token cookie (logout)
     */
    @Override
    public void deleteAccessTokenCookie(HttpServletResponse response) {
        deleteCookie(response, jwtProperties.getCookie().getAccessTokenName());
    }

    /**
     * Delete refresh token cookie (logout)
     */
    @Override
    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        deleteCookie(response, jwtProperties.getCookie().getRefreshTokenName());
    }

    /**
     * Delete all authentication cookies
     */
    @Override
    public void deleteAllAuthCookies(HttpServletResponse response) {
        deleteAccessTokenCookie(response);
        deleteRefreshTokenCookie(response);
        LOGGER.debug("All authentication cookies deleted");
    }

    /**
     * Helper method to delete a cookie
     */
    private void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath(jwtProperties.getCookie().getPath());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);  // Delete immediately

        if (jwtProperties.getCookie().getDomain() != null) {
            cookie.setDomain(jwtProperties.getCookie().getDomain());
        }

        response.addCookie(cookie);
        LOGGER.debug("Cookie {} deleted", cookieName);
    }
}
