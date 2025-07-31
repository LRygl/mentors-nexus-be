package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.CourseDiscountDTO;
import com.mentors.applicationstarter.Model.CourseDiscount;

public class CourseDiscountMapper {

    public static CourseDiscountDTO toResponseDTO(CourseDiscount discount) {
        return CourseDiscountDTO.builder()
                .id(discount.getId())
                .course(CourseMapper.toSummaryDto(discount.getCourse()))
                .discountPercentage(discount.getDiscountPercentage())
                .discountAmount(discount.getDiscountAmount())
                .validFrom(discount.getValidFrom())
                .validTo(discount.getValidTo())
                .active(discount.getActive())
                .createdAt(discount.getCreatedAt())
                .updatedAt(discount.getUpdatedAt())
                .createdBy(discount.getCreatedBy())
                .description(discount.getDescription())
                .build();
    }
}
