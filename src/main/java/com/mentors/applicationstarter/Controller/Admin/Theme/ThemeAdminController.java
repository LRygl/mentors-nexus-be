package com.mentors.applicationstarter.Controller.Admin.Theme;

import com.mentors.applicationstarter.DTO.Theme.ThemeCreateRequestDTO;
import com.mentors.applicationstarter.DTO.Theme.ThemeDTO;
import com.mentors.applicationstarter.Model.Theme;
import com.mentors.applicationstarter.Service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/settings/theme")
@RequiredArgsConstructor
public class ThemeAdminController {

    private final ThemeService themeService;


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Theme>> getAllThemes(){
        return ResponseEntity.ok(themeService.getAllThemes());
    }

    @PostMapping
    public ResponseEntity<ThemeDTO> createTheme(@RequestBody ThemeCreateRequestDTO theme){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(themeService.createTheme(theme));
    }
}
