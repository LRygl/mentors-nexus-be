package com.mentors.applicationstarter.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseDiscountDTO {

    private Long id;
    private CourseSummaryDTO course;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private Instant validFrom;
    private Instant validTo;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String description;
}
