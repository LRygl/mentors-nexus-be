package com.mentors.applicationstarter.DTO.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

    private Long id;
    private Long courseId;
    private String courseName;
    private BigDecimal originalPrice;
    private BigDecimal pricePaid;
}
