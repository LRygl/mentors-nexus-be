package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.CompanyResponseDTO;
import com.mentors.applicationstarter.Model.Company;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public class CompanyMapper {


    public static CompanyResponseDTO toCompanyDto(Company company) {
        return CompanyResponseDTO.builder()
                .name(company.getName())
                .vatNumber(company.getVatNumber())
                .registrationNumber(company.getRegistrationNumber())
                .build();
    }


}
