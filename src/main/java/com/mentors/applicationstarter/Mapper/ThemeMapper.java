package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.Theme.ThemeDTO;
import com.mentors.applicationstarter.Model.Theme;
import org.springframework.stereotype.Component;

@Component
public class ThemeMapper {
    public ThemeDTO toDTO(Theme theme) {
        if (theme == null) return null;

        ThemeDTO dto = new ThemeDTO();
        dto.setId(theme.getId());
        dto.setName(theme.getName());
        dto.setConfiguration(theme.getConfiguration()); // Already JSON string
        dto.setIsActive(theme.getIsActive());
        dto.setDescription(theme.getDescription());
        dto.setCreatedBy(theme.getCreatedBy());
        dto.setUpdatedBy(theme.getUpdatedBy());
        dto.setCreatedAt(theme.getCreatedAt());
        dto.setUpdatedAt(theme.getUpdatedAt());
        dto.setIsSystemTheme(theme.getIsSystemTheme());

        return dto;
    }
}
