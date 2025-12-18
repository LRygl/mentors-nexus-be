package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.Theme.ThemeCreateRequestDTO;
import com.mentors.applicationstarter.DTO.Theme.ThemeDTO;
import com.mentors.applicationstarter.Model.Theme;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ThemeService {

    List<Theme> getAllThemes();

    ThemeDTO createTheme(ThemeCreateRequestDTO theme);
}
