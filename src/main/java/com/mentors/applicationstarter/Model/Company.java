package com.mentors.applicationstarter.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "companyGenerator")
    @SequenceGenerator(name = "companyGenerator", sequenceName = "application_company_sequence", allocationSize = 50)
    @Column(nullable = false, updatable = false)
    private Long id;
    private UUID UUID;
    private String name;
    private User users;
    private String vatNumber; //DIČ
    private String registrationNumber; //IČO

    private Instant createdDate;
    private Instant updatedDate;

    private Boolean forceVatNumFromRegister;

    private String billingInfo;
    private String currentPlan;
    private String invoice;

}
