package com.mentors.applicationstarter.DTO.Order;

import com.mentors.applicationstarter.DTO.Course.EnrolledCourseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO returned after a successful checkout.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponseDTO {

    private Long orderId;
    private Long invoiceId;
    private String invoiceNumber;
    private BigDecimal totalAmount;
    private String status;
    private List<EnrolledCourseDTO> enrolledCourses;
}
