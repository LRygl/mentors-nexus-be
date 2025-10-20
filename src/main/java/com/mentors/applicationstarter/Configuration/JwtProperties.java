package com.mentors.applicationstarter.Configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private Long accessTokenExpiration;  // in milliseconds
    private Long refreshTokenExpiration; // in milliseconds
    private Cookie cookie;

    @Data
    public static class Cookie {
        private String accessTokenName = "access_token";
        private String refreshTokenName = "refresh_token";
        private String domain;
        private Boolean secure = false;  // Set true in production
        private String sameSite = "Lax"; // Strict, Lax, or None
        private String path = "/";
    }
}
