package com.mentors.applicationstarter.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FAQCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, unique = true, length = 50)
    private String slug; // URL-friendly version of name

    @Column(length = 50)
    private String iconClass; // CSS class for icon (e.g., "fas fa-question-circle")

    @Column(length = 7)
    private String colorCode; // Hex color code for UI theming

    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isVisible = true; // Show in public category lists

    // SEO fields
    @Column(length = 160)
    private String metaDescription;

    @Column(length = 200)
    private String metaKeywords;

    // Statistics (computed fields)
    @Formula("(SELECT COUNT(*) FROM faq f WHERE f.category_id = id)")
    private Integer faqCount;

    @Formula("(SELECT COUNT(*) FROM faq f WHERE f.category_id = id AND f.is_published = true)")
    private Integer publishedFaqCount;

    // Relationships
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<FAQ> faqs = new ArrayList<>();

    // Audit fields
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @PrePersist
    private void generateUUID() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        if (this.slug == null && this.name != null) {
            this.slug = generateSlug(this.name);
        }
    }

    @PreUpdate
    private void updateSlug() {
        if (this.name != null) {
            this.slug = generateSlug(this.name);
        }
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "") // Remove special characters
                .replaceAll("\\s+", "-") // Replace spaces with hyphens
                .replaceAll("-+", "-") // Replace multiple hyphens with single
                .replaceAll("^-|-$", ""); // Remove leading/trailing hyphens
    }

    // Helper methods
    public boolean hasPublishedFAQs() {
        return publishedFaqCount != null && publishedFaqCount > 0;
    }

}
