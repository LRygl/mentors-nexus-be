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
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "invoiceGenerator")
    @SequenceGenerator(name = "invoiceGenerator", sequenceName = "application_invoice_sequence", allocationSize = 50)
    @Column(nullable = false, updatable = false)
    private Long id;
    private UUID UUID;
    private String invoiceNumber;
    //private User user; //nullable if company invoice
    //private Company company; //nullable if individual customer invoice
    private Float totalAmount;
    private Instant issuedDate;
    private Instant dueDate;
    private Boolean paid;
    private Instant paidDate;
    private String invoicePdfName;
    private String invoicePdfLink;
    //private Purchase relatedPurchases; //list of items purchased in the invoice
    //private Integer billedUsers //Number of billed users for company

}
