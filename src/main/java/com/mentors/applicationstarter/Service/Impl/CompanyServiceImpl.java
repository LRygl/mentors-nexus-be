package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.CompanyRequestDTO;
import com.mentors.applicationstarter.DTO.CompanyResponseDTO;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Mapper.CompanyMapper;
import com.mentors.applicationstarter.Model.Company;
import com.mentors.applicationstarter.Repository.CompanyRepository;
import com.mentors.applicationstarter.Service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<CompanyResponseDTO> getPagedCompanies(Pageable pageable) {
        Page<CompanyResponseDTO> companyPage = companyRepository.findAll(pageable).map(CompanyMapper::toCompanyDto);
        return companyPage;
    }

    @Override
    public CompanyResponseDTO getCompanyById(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.COMPANY_DOES_NOT_EXIST));
        return CompanyMapper.toCompanyDto(company);
    }

    @Override
    public CompanyResponseDTO getCompanyAresData(Long companyId, String companyVAT) {
        return null;
    }

    @Override
    public CompanyResponseDTO deleteCompany(Long companyId) {
        Company company = findCompany(companyId);
        companyRepository.delete(company);
        return CompanyMapper.toCompanyDto(company);
    }

    @Override
    public CompanyResponseDTO createCompany(CompanyRequestDTO request) {
//todo vat number is mandatory
        //todo validate if exists by vatno
        Company company = Company.builder()
                .UUID(UUID.randomUUID())
                .name(request.getName())
                .vatNumber(request.getVatNumber())
                .createdDate(Instant.now())
                .registrationNumber(request.getRegistrationNumber())
                .build();

        return CompanyMapper.toCompanyDto(companyRepository.save(company));
    }

    @Override
    public CompanyResponseDTO updateCompany(CompanyRequestDTO request, Long companyId) {

        Company company = findCompany(companyId);

        if (request.getName() != null) {
            company.setName(request.getName());
        }
        if (request.getVatNumber() != null) {
            company.setVatNumber(request.getVatNumber());
        }
        if (request.getRegistrationNumber() != null) {
            company.setRegistrationNumber(request.getRegistrationNumber());
        }

        company.setUpdatedDate(Instant.now());
        return CompanyMapper.toCompanyDto(companyRepository.save(company));
    }

    private <T> Company findCompany(T identifier) {
        return switch (identifier) {
            case Long l -> companyRepository.findById(l)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.COMPANY_DOES_NOT_EXIST));
            default -> throw new IllegalStateException("Unexpected value: " + identifier);
        };
    }

}
