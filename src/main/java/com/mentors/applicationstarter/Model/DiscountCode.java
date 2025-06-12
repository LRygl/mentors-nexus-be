package com.mentors.applicationstarter.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class DiscountCode {

    //TODO how to track code spendings

    @Id
    private Long id;
    private String discountCode;
    private Instant validUntil;
    private Integer discountPercentage;
    private Long usageLimit; //0 - unlimited, 1+ limited
    private String marketingTarget;
    private Boolean companySpecific;
    //private Company companyId;
    //private Course applicableCourses;


}
