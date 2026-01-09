package com.mentors.applicationstarter.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "course_enrollment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"})
)
public class CourseEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Builder.Default
    private Instant enrolledAt = Instant.now();

    // How the user got access
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EnrollmentType type = EnrollmentType.PURCHASED;

    // Optional: track progress
    @Builder.Default
    private Double progressPercent = 0.0;

    private Instant lastAccessedAt;

    // For purchases - link to payment/order
    private Long orderId;

    public enum EnrollmentType {
        PURCHASED,      // User bought the course
        GIFTED,         // Someone gifted it
        ADMIN_ASSIGNED, // Admin gave access
        FREE,           // Free course enrollment
        PROMOTIONAL     // Promo code / coupon
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseEnrollment that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}