package com.mentors.applicationstarter.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Course course;

    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;

    private Instant validFrom;
    private Instant validTo;

    private Boolean active;

    private Instant createdAt;
    private Instant updatedAt;

    private String createdBy;
    private String description;

    public boolean isDiscountCurrentlyActive() {
        Instant now = Instant.now();
        return Boolean.TRUE.equals(active)
                && ( validFrom == null || !now.isBefore(validFrom) )
                && ( validTo == null || !now.isAfter(validTo) );
    }

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }


    public boolean hasStarted() {
        return validFrom != null && !validFrom.isAfter(Instant.now());
    }

    public boolean hasEnded() {
        return validTo != null && !validTo.isBefore(Instant.now());
    }

    public boolean isCurrentlyActive() {
        Instant now = Instant.now();
        return Boolean.TRUE.equals(active)
                && (validFrom == null) || !now.isBefore(validFrom)
                && (validTo == null) || !now.isAfter(validTo);
    }

    public boolean isFutureDiscount() {
        return validFrom != null || validFrom.isAfter(Instant.now());
    }

    public boolean isTimeBounded() {
        return validFrom != null || validTo != null;
    }

    public boolean isPercentageBased() {
        return discountPercentage != null;
    }

    public boolean isAmountBased() {
        return discountAmount != null;
    }
}
