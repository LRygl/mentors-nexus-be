package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.CompanyRequestDTO;
import com.mentors.applicationstarter.DTO.CompanyResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CompanyService {


    List<CompanyResponseDTO> getAllCourses();

    CompanyResponseDTO createCompany(CompanyRequestDTO request);

    CompanyResponseDTO updateCompany(CompanyRequestDTO request, Long companyId);

    CompanyResponseDTO getCompanyById(Long companyId);

    CompanyResponseDTO getCompanyAresData(Long companyId, String companyVAT);

    CompanyResponseDTO deleteCompany(Long companyId);

    Page<CompanyResponseDTO> getPagedCompanies(Pageable pageable);

    CompanyResponseDTO enrollUserToCompany(Long companyId, Long userId);
}
