package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.CompanyRequestDTO;
import com.mentors.applicationstarter.DTO.CompanyResponseDTO;
import com.mentors.applicationstarter.Mapper.CompanyMapper;
import com.mentors.applicationstarter.Model.Company;
import com.mentors.applicationstarter.Repository.CompanyRepository;
import com.mentors.applicationstarter.Service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public List<CompanyResponseDTO> getAllCourses() {
        return companyRepository.findAll().stream()
                .map(CompanyMapper::toCompanyDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyResponseDTO createCompany(CompanyRequestDTO request) {

        Company company = Company.builder()
                .UUID(UUID.randomUUID())
                .name(request.getName())
                .vatNumber(request.getVatNumber())
                .createdDate(Instant.now())
                .registrationNumber(request.getRegistrationNumber())
                .build();

        return CompanyMapper.toCompanyDto(companyRepository.save(company));
    }
}
