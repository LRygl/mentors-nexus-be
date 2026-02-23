package com.mentors.applicationstarter.Configuration;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfiguration.class);

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvided;

    // Add this property to read from application.properties
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    /*
    *         .authorizeHttpRequests(auth -> auth
    *        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")  // ✅ All admin endpoints
    *        .requestMatchers("/api/v1/enrollments/**").authenticated()
    *        .requestMatchers("/api/v1/public/**").permitAll()
    *        // ... other rules
    */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Inject CORS configuration into the SecurityFilterChain instead of Customizer.withDefaults()
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no authentication needed)
                        .requestMatchers(
                                "/actuator/**",
                                "/api/v1/actuator/**"
                        ).permitAll()

                        // Auth endpoints (login, register, refresh)
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password",
                                "/api/v1/auth/activate"
                        ).permitAll()
                        // Public content endpoints (if needed)
                        .requestMatchers(
                                "/api/v1/legal/public/**",
                                "/api/v1/faq/public/**"
                        ).permitAll()

                        // Admin endpoints require ADMIN role
                        .requestMatchers(
                                "/api/v1/admin/**"
                        ).hasAnyRole("ADMIN")

                        // User is logged in
                        .requestMatchers(
                                "/api/v1/user/**"
                        ).hasAnyAuthority("USER")

                        // /auth/me REQUIRES authentication (critical!)
                        .requestMatchers("/api/v1/auth/me").authenticated()

                        // Everything else requires authentication
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvided)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> origins = List.of(allowedOrigins.split(","));

        // Add debug logging
        LOGGER.info("=== CORS Configuration ===");
        LOGGER.info("Allowed Origins: {}", origins);
        LOGGER.info("Allowed Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS");
        LOGGER.info("Exposed Headers: X-JWT-TOKEN, Content-Range, Accept-Ranges, Content-Length, Content-Type");
        LOGGER.info("==========================");

        configuration.setAllowedOrigins(origins);;
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("X-JWT-TOKEN");
        configuration.addExposedHeader("Content-Range");      // For partial content responses
        configuration.addExposedHeader("Accept-Ranges");      // Tells browser we support Range requests
        configuration.addExposedHeader("Content-Length");     // Total content length
        configuration.addExposedHeader("Content-Type");       // MIME type of video
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
