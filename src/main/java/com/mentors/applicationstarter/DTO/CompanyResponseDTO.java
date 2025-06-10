package com.mentors.applicationstarter.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponseDTO {

    private String name;
    private String vatNumber; //DIČ
    private String registrationNumber; //IČO

}
