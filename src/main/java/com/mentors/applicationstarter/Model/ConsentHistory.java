package com.mentors.applicationstarter.Model;

import com.mentors.applicationstarter.Enum.ConsentType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class ConsentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;

    @Enumerated(EnumType.STRING)
    private ConsentType consentType;
    private Boolean consentGiven;
    private String source; // e.g., "WEB", "API", "MOBILE"
    private LocalDateTime timestamp;
    private String ipAddress;
    private String userAgent;
}
