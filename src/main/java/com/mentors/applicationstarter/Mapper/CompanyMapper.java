package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.CompanyResponseDTO;
import com.mentors.applicationstarter.DTO.UserResponseDTO;
import com.mentors.applicationstarter.Model.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompanyMapper {

    private final UserMapper userMapper;

    public CompanyResponseDTO toCompanyDto(Company company) {
        return CompanyResponseDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .UUID(company.getUUID())
                .vatNumber(company.getVatNumber())
                .registrationNumber(company.getRegistrationNumber())
                .billingAddress(company.getBillingAddress())
                .createdDate(company.getCreatedDate())
                .companyMembers(company.getCompanyMembers().stream()
                        .map(userMapper::mapUserToDto)
                        .collect(Collectors.toList())
                )
                .build();
    }

}
