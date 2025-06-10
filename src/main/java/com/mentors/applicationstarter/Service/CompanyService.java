package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.CompanyRequestDTO;
import com.mentors.applicationstarter.DTO.CompanyResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CompanyService {


    List<CompanyResponseDTO> getAllCourses();

    CompanyResponseDTO createCompany(CompanyRequestDTO request);
}
