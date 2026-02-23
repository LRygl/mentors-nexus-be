package com.mentors.applicationstarter.DTO.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Detailed invoice DTO for the invoice detail page.
 * Includes all invoice data, billing info, and line items from the order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetailDTO {

    private Long id;
    private String uuid;
    private String invoiceNumber;

    // Customer details
    private String billingFirstName;
    private String billingLastName;
    private String billingEmail;
    private String billingStreet;
    private String billingCity;
    private String billingPostalCode;
    private String billingCountry;

    // Financial
    private BigDecimal totalAmount;
    private String currency;
    private Boolean paid;

    // Dates
    private Instant issuedDate;
    private Instant dueDate;
    private Instant paidDate;

    // Order items
    private List<OrderItemDTO> items;

    // PDF
    private String invoicePdfLink;
    private String invoicePdfName;
}
