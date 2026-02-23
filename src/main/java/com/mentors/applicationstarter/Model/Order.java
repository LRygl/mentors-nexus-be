package com.mentors.applicationstarter.Model;

import com.mentors.applicationstarter.Enum.OrderStatus;
import com.mentors.applicationstarter.Enum.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<OrderItem> items = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod selectedPaymentMethod;

    // Billing details (snapshot at time of purchase)
    private String billingFirstName;
    private String billingLastName;
    private String billingEmail;
    private String billingPhone;
    private String billingStreet;
    private String billingCity;
    private String billingPostalCode;
    private String billingCountry;

    // Consent tracking
    @Column(nullable = false)
    @Builder.Default
    private Boolean termsAccepted = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean privacyAccepted = false;

    private Instant consentTimestamp;
    private Instant completedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return getId() != null && getId().equals(order.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
