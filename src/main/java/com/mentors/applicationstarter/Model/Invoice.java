package com.mentors.applicationstarter.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "invoiceGenerator")
    @SequenceGenerator(name = "invoiceGenerator", sequenceName = "application_invoice_sequence", allocationSize = 50)
    @Column(nullable = false, updatable = false)
    private Long id;
    private UUID UUID;
    private String invoiceNumber;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company; // nullable - for company invoices

    // Financial
    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Builder.Default
    private String currency = "CZK";

    // Dates & status
    private Instant issuedDate;
    private Instant dueDate;
    @Builder.Default
    private Boolean paid = false;
    private Instant paidDate;

    // PDF storage
    private String invoicePdfName;
    private String invoicePdfLink;

    // Billing address snapshot (frozen at invoice time)
    private String billingFirstName;
    private String billingLastName;
    private String billingEmail;
    private String billingStreet;
    private String billingCity;
    private String billingPostalCode;
    private String billingCountry;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invoice invoice)) return false;
        return id != null && id.equals(invoice.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
