package com.mentors.applicationstarter.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CookieService {
    void addAccessTokenCookie(HttpServletResponse response, String token);
    void addRefreshTokenCookie(HttpServletResponse response, String token);
    Optional<String> extractAccessToken(HttpServletRequest request);
    Optional<String> extractRefreshToken(HttpServletRequest request);
    void deleteAccessTokenCookie(HttpServletResponse response);
    void deleteRefreshTokenCookie(HttpServletResponse response);
    void deleteAllAuthCookies(HttpServletResponse response);
}
