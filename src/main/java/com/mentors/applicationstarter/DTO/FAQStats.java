package com.mentors.applicationstarter.DTO;

import com.mentors.applicationstarter.Model.FAQ;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for FAQ statistics used in admin dashboard
 * Contains comprehensive analytics data for FAQ system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FAQStats {

    /**
     * Total number of FAQs in the system
     */
    private Long totalFAQs;

    /**
     * Number of published FAQs
     */
    private Long publishedFAQs;

    /**
     * Number of draft FAQs
     */
    private Long draftFAQs;

    /**
     * Number of featured FAQs
     */
    private Long featuredFAQs;

    /**
     * Total view count across all FAQs
     */
    private Long totalViews;

    /**
     * Total helpful votes across all FAQs
     */
    private Long totalHelpfulVotes;

    /**
     * List of most viewed FAQs (limited to top 5-10)
     */
    private List<FAQ> mostViewedFAQs;

    /**
     * List of most helpful FAQs (limited to top 5-10)
     */
    private List<FAQ> mostHelpfulFAQs;

    /**
     * FAQ counts broken down by category
     */
    private List<CategoryFAQCount> faqsByCategory;

    /**
     * Average helpfulness ratio across all FAQs
     */
    private Double averageHelpfulnessRatio;

    /**
     * Number of FAQs created in the last 30 days
     */
    private Long recentFAQs;

    /**
     * Total number of active categories
     */
    private Long activeCategories;
}
