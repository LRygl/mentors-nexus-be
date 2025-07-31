package com.mentors.applicationstarter.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for category FAQ count statistics
 * Used in analytics and reporting features
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryFAQCount {

    /**
     * Category name for display
     */
    private String categoryName;

    /**
     * Category URL slug
     */
    private String categorySlug;

    /**
     * Category UUID for API operations
     */
    private String categoryUuid;

    /**
     * Total number of FAQs in this category
     */
    private Long totalFAQs;

    /**
     * Number of published FAQs in this category
     */
    private Long publishedFAQs;

    /**
     * Number of draft FAQs in this category
     */
    private Long draftFAQs;

    /**
     * Number of featured FAQs in this category
     */
    private Long featuredFAQs;

    /**
     * Total view count for FAQs in this category
     */
    private Long totalViews;

    /**
     * Average helpfulness ratio for FAQs in this category
     */
    private Double averageHelpfulnessRatio;

    /**
     * Whether this category is active
     */
    private Boolean isActive;

    /**
     * Category display order
     */
    private Integer displayOrder;
}
