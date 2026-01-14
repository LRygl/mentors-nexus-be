package com.mentors.applicationstarter.DTO.Theme;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ThemeDTO {
    private Long id;
    private String name;
    private String configuration; // JSON string
    private Boolean isActive;
    private String description;
    private UUID createdBy;
    private UUID updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isSystemTheme;
}
