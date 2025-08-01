package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.FAQStats;
import com.mentors.applicationstarter.Enum.FAQPriority;
import com.mentors.applicationstarter.Enum.FAQStatus;
import com.mentors.applicationstarter.Model.FAQ;
import com.mentors.applicationstarter.Model.FAQCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * FAQ Service Interface
 * Provides comprehensive FAQ management functionality for both public and admin use
 */

@Service
public interface FAQService {
    // ================================
    // PUBLIC API METHODS (for frontend users)
    // ================================

    /**
     * Get all published FAQs ordered by priority and display order
     */
    List<FAQ> getAllPublishedFAQs();

    /**
     * Get published FAQs by category entity
     */
    List<FAQ> getFAQsByCategory(FAQCategory category);

    /**
     * Get published FAQs by category slug (SEO-friendly URLs)
     */
    List<FAQ> getFAQsByCategorySlug(String categorySlug);

    /**
     * Get published FAQs by category UUID
     */
    List<FAQ> getFAQsByCategoryUuid(UUID categoryUuid);

    /**
     * Get featured FAQs for homepage display
     */
    List<FAQ> getFeaturedFAQs();

    /**
     * Search published FAQs across all categories
     */
    List<FAQ> searchFAQs(String searchTerm);

    /**
     * Search published FAQs within a specific category
     */
    List<FAQ> searchFAQsInCategory(String searchTerm, String categorySlug);

    /**
     * Get FAQ by UUID (public access to published FAQs only)
     */
    Optional<FAQ> getFAQByUuid(UUID uuid);

    /**
     * Get FAQ by slug (SEO-friendly access)
     */
    Optional<FAQ> getFAQBySlug(String slug);

    /**
     * Record a view for analytics (async operation)
     */
    void recordFAQView(UUID uuid);

    /**
     * Record helpful/not helpful vote
     */
    void voteFAQHelpful(UUID uuid, boolean isHelpful);

    /**
     * Get most viewed FAQs for analytics
     */
    List<FAQ> getMostViewedFAQs(int limit);

    /**
     * Get most viewed FAQs in a specific category
     */
    List<FAQ> getMostViewedFAQsInCategory(String categorySlug, int limit);

    /**
     * Get most helpful FAQs based on votes
     */
    List<FAQ> getMostHelpfulFAQs(int limit);

    // ================================
    // ADMIN API METHODS (for backend management)
    // ================================

    Page<FAQ> getAllFAQsForAdmin(Pageable pageable);
    Page<FAQ> getFAQsByStatus(FAQStatus status, Pageable pageable);
    Page<FAQ> getFAQsByCategoryForAdmin(UUID categoryUuid, Pageable pageable);
    Page<FAQ> getFAQsByFilters(FAQStatus status, UUID categoryUuid, FAQPriority priority,
                               String searchTerm, Pageable pageable);
    FAQ createFAQ(FAQ faq, UUID createdBy);
    FAQ updateFAQ(UUID uuid, FAQ faq, UUID updatedBy);
    void deleteFAQ(UUID uuid);
    FAQ publishFAQ(UUID uuid, UUID updatedBy);
    FAQ unpublishFAQ(UUID uuid, UUID updatedBy);
    FAQ featureFAQ(UUID uuid, UUID updatedBy);
    FAQ unfeatureFAQ(UUID uuid, UUID updatedBy);
    void reorderFAQs(List<UUID> orderedUuids);
    void moveFAQsBetweenCategories(UUID oldCategoryUuid, UUID newCategoryUuid, UUID updatedBy);

    // ================================
    // VALIDATION METHODS
    // ================================

    /**
     * Check if FAQ slug is unique within a category
     */
    boolean isFAQSlugUnique(String slug, UUID categoryUuid, UUID excludeUuid);

    // ================================
    // ANALYTICS AND STATISTICS
    // ================================

    /**
     * Get comprehensive FAQ statistics for dashboard
     */
    FAQStats getFAQStats();

    /**
     * FAQ count by status
     */
    Long getFAQCountByStatus(FAQStatus status);

    /**
     * Get FAQ count by priority
     */
    Long getFAQCountByPriority(FAQPriority priority);

    /**
     * Get total published FAQ count
     */
    Long getPublishedFAQCount();

    /**
     * Get featured FAQ count
     */
    Long getFeaturedFAQCount();
}
