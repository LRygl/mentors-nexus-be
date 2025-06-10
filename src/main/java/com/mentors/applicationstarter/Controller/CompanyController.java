package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.DTO.CompanyRequestDTO;
import com.mentors.applicationstarter.DTO.CompanyResponseDTO;
import com.mentors.applicationstarter.Service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/all")
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {
        return new ResponseEntity<>(companyService.getAllCourses(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(@RequestBody CompanyRequestDTO request) {
        return new ResponseEntity<>(companyService.createCompany(request), HttpStatus.CREATED);
    }


}
