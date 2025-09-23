package com.mentors.applicationstarter.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(name = "question", columnDefinition = "TEXT", nullable = false, length = 500)
    private String question;

    @Column(name = "answer", columnDefinition = "TEXT", nullable = false)
    private String answer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = true)
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
    @Column(name = "search_keywords", columnDefinition = "TEXT", length = 1000)
    private String searchKeywords;

    @Column(name = "meta_description", columnDefinition = "TEXT", length = 160)
    private String metaDescription;

    @Column(name = "slug", columnDefinition = "VARCHAR(255)", unique = true)
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

    // FIX: Combine both operations into a single @PrePersist method
    @PrePersist
    private void onPrePersist() {
        // Generate UUID if not present
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }

        // Generate slug if not present and question exists
        if (this.slug == null && this.question != null) {
            this.slug = generateSlug(this.question);
        }
    }

    // FIX: Separate method for updates only
    @PreUpdate
    private void onPreUpdate() {
        // Update slug if question changed
        if (this.question != null && !this.question.trim().isEmpty()) {
            this.slug = generateSlug(this.question);
        }
    }

    /**
     * Generates a URL-safe slug from the question text
     * FIX: Added proper bounds checking and validation
     */
    private String generateSlug(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "faq-" + System.currentTimeMillis(); // Fallback slug
        }

        // Clean and normalize the text
        String normalized = text.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "") // Remove special characters
                .replaceAll("\\s+", "-")         // Replace spaces with hyphens
                .replaceAll("-+", "-")           // Replace multiple hyphens with single
                .replaceAll("^-|-$", "");        // Remove leading/trailing hyphens

        // FIX: Safe substring with proper bounds checking
        final int MAX_SLUG_LENGTH = 50; // Safe maximum length

        if (normalized.length() <= MAX_SLUG_LENGTH) {
            return normalized.isEmpty() ? "faq-" + System.currentTimeMillis() : normalized;
        }

        // Find last word boundary within limit to avoid cutting words
        String truncated = normalized.substring(0, MAX_SLUG_LENGTH);
        int lastHyphen = truncated.lastIndexOf('-');

        if (lastHyphen > 0 && lastHyphen > MAX_SLUG_LENGTH - 10) {
            // Cut at word boundary if it's reasonable
            return normalized.substring(0, lastHyphen);
        }

        // Otherwise just truncate
        return truncated;
    }


    public String getCategorySlug() {
        return category != null ? category.getSlug() : "Uncategorized";
    }
}