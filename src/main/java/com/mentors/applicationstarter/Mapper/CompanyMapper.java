package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.CompanyResponseDTO;
import com.mentors.applicationstarter.DTO.UserResponseDTO;
import com.mentors.applicationstarter.Model.Company;

import java.util.stream.Collectors;


public class CompanyMapper {


    public static CompanyResponseDTO toCompanyDto(Company company) {
        return CompanyResponseDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .UUID(company.getUUID())
                .vatNumber(company.getVatNumber())
                .registrationNumber(company.getRegistrationNumber())
                .billingAddress(company.getBillingAddress())
                .createdDate(company.getCreatedDate())
                .companyMembers(company.getCompanyMembers().stream()
                        .map(UserMapper::mapUserToDto)
                        .collect(Collectors.toList())
                )
                .build();
    }

}
