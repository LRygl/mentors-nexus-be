package com.mentors.applicationstarter.DTO;

import com.mentors.applicationstarter.Model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRequestDTO {

    private String name;
    private String vatNumber; //DIČ
    private String registrationNumber; //IČO
}
