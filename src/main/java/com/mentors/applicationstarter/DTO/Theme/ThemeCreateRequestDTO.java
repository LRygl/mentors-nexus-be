package com.mentors.applicationstarter.DTO.Theme;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ThemeCreateRequestDTO {
    @NotBlank(message = "Theme name is required")
    private String name;

    @NotBlank(message = "Configuration is required")
    private String configuration; // JSON string

    private String description;
}
