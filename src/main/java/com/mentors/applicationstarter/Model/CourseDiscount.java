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

    @ManyToOne(optional = true)
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

/*
    CourseDiscount {
        id : Long
        course : Course
        discountPercentage : BigDecimal  // 20% → store as 20.0
        discountAmount : BigDecimal (optional, if you want fixed amount off)
        validFrom : LocalDateTime
        validTo : LocalDateTime
        isActive : Boolean  // optional - used for admin toggling
        createdAt : LocalDateTime
        updatedAt : LocalDateTime
    }
*/


}
