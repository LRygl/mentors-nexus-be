package com.mentors.applicationstarter.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mentors.applicationstarter.Enum.FAQPriority;
import com.mentors.applicationstarter.Enum.FAQStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FAQ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    // Relationship with FAQCategory
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private FAQCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FAQStatus status = FAQStatus.DRAFT;

    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    // SEO and searchability
    @Column(length = 1000)
    private String searchKeywords;

    @Column(length = 160)
    private String metaDescription;

    @Column(length = 100)
    private String slug; // URL-friendly version for SEO

    // Analytics
    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Integer helpfulVotes = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer notHelpfulVotes = 0;

    // Priority for search and display
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FAQPriority priority = FAQPriority.NORMAL;

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
        if (this.slug == null && this.question != null) {
            this.slug = generateSlug(this.question);
        }
    }

    @PreUpdate
    private void updateSlug() {
        if (this.question != null) {
            this.slug = generateSlug(this.question);
        }
    }

    private String generateSlug(String question) {
        return question.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "") // Remove special characters
                .replaceAll("\\s+", "-") // Replace spaces with hyphens
                .replaceAll("-+", "-") // Replace multiple hyphens with single
                .replaceAll("^-|-$", "") // Remove leading/trailing hyphens
                .substring(0, Math.min(question.length(), 100)); // Limit length
    }

    // Computed fields for frontend
    public Double getHelpfulnessRatio() {
        int totalVotes = helpfulVotes + notHelpfulVotes;
        if (totalVotes == 0) return 0.0;
        return (double) helpfulVotes / totalVotes;
    }

    public Boolean getIsPopular() {
        return viewCount > 100; // Configurable threshold
    }

    public String getFullUrl() {
        return "/faq/" + category.getSlug() + "/" + slug;
    }

    public String getCategoryName() {
        return category != null ? category.getName() : null;
    }

    public String getCategorySlug() {
        return category != null ? category.getSlug() : null;
    }
}