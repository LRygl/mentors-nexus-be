package com.mentors.applicationstarter.DTO.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Lightweight invoice DTO for listing invoices.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceListDTO {

    private Long id;
    private String invoiceNumber;
    private BigDecimal totalAmount;
    private String currency;
    private Boolean paid;
    private Instant issuedDate;
    private Instant dueDate;
    private Instant paidDate;
    private int itemCount;
}
