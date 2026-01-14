package com.mentors.applicationstarter.Service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentors.applicationstarter.DTO.Theme.ThemeCreateRequestDTO;
import com.mentors.applicationstarter.DTO.Theme.ThemeDTO;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ConfigurationException;
import com.mentors.applicationstarter.Mapper.ThemeMapper;
import com.mentors.applicationstarter.Model.Theme;
import com.mentors.applicationstarter.Repository.ThemeRepository;
import com.mentors.applicationstarter.Service.ThemeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThemeServiceImpl implements ThemeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThemeServiceImpl.class);


    private final ThemeRepository themeRepository;
    private final ObjectMapper objectMapper;
    private final ThemeMapper themeMapper;

    @Override
    public List<Theme> getAllThemes() {
        return themeRepository.findAll();
    }

    @Transactional
    public ThemeDTO createTheme(ThemeCreateRequestDTO request) {
        // Validate theme name is unique
        if (themeRepository.existsByName(request.getName())) {
            throw new ConfigurationException(ErrorCodes.CONFIGRATION_ALREADY_EXISTS);
        }

        // Validate JSON configuration
        validateThemeConfiguration(request.getConfiguration());

        Theme theme = new Theme();
        theme.setName(request.getName());
        theme.setUuid(UUID.randomUUID());
        theme.setConfiguration(request.getConfiguration());
        theme.setDescription(request.getDescription());
        theme.setIsActive(false);
        theme.setIsSystemTheme(false);

        Theme savedTheme = themeRepository.save(theme);

        return themeMapper.toDTO(savedTheme);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void validateThemeConfiguration(String configJson) {
        try {
            // Attempt to parse JSON to ensure it's valid
            objectMapper.readTree(configJson);

            // Additional validation can be added here
            // For example, check for required fields like colors, typography, etc.

        } catch (Exception e) {
            throw new ConfigurationException(ErrorCodes.CONFIGURATION_JSON_NOT_VALID);
        }
    }

}
