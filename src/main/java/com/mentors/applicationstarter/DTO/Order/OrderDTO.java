package com.mentors.applicationstarter.DTO.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private String uuid;
    private String status;
    private BigDecimal totalAmount;
    private String selectedPaymentMethod;
    private Instant createdAt;
    private Instant completedAt;
    private Long invoiceId;
    private String invoiceNumber;
    private List<OrderItemDTO> items;
}
