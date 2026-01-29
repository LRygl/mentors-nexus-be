package com.mentors.applicationstarter.Configuration;

import com.mentors.applicationstarter.Service.CookieService;
import com.mentors.applicationstarter.Service.Impl.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtServiceImpl jwtServiceImpl;
    private final UserDetailsService userDetailsService;
    private final CookieService cookieService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Skip JWT filter for actuator health endpoints
        if (path.startsWith("/actuator/health")) {
            return true;
        }

        // Skip OPTIONS requests (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // debug logging to see which endpoint is being called
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        LOGGER.debug("Processing {} {}", method, requestURI);

        // Extract JWT from cookie instead of Authorization header
        String jwt = cookieService.extractAccessToken(request).orElse(null);

        if (jwt == null) {
            // ✅ More detailed logging
            LOGGER.warn("No JWT token found for {} {} - cookies present: {}",
                    method,
                    requestURI,
                    request.getCookies() != null ?
                            Arrays.stream(request.getCookies())
                                    .map(Cookie::getName)
                                    .collect(Collectors.joining(", "))
                            : "none"
            );
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract username from JWT
            String userEmail = jwtServiceImpl.extractUsername(jwt);

            // If user is not authenticated yet
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // Validate token
                if (jwtServiceImpl.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // Only log on first authentication, not every request
                    LOGGER.debug("User {} authenticated for {} {}", userEmail, method, requestURI);
                } else {
                    LOGGER.warn("JWT token validation failed for user {}", userEmail);
                }
            }
        } catch (Exception e) {
            // Token validation failed - continue without authentication
            LOGGER.error("JWT validation failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
