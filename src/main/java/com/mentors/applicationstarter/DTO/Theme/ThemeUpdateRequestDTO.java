package com.mentors.applicationstarter.DTO.Theme;

import lombok.Data;

@Data
public class ThemeUpdateRequestDTO {
    private String name;
    private String configuration; // JSON string
    private String description;
}