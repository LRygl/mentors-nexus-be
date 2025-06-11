package com.mentors.applicationstarter.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponseDTO {

    private Long id;
    private UUID UUID;
    private String name;
    private String vatNumber; //DIČ
    private String registrationNumber; //IČO
    private String billingAddress;
    private Instant createdDate;
    private Instant updatedDate;

}
