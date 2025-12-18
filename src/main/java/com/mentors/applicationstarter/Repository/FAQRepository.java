package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Enum.FAQPriority;
import com.mentors.applicationstarter.Enum.FAQStatus;
import com.mentors.applicationstarter.Model.FAQ;
import com.mentors.applicationstarter.Model.FAQCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * FAQ Repository Interface
 * Provides comprehensive data access methods for FAQ entities
 * Includes public queries, admin queries, analytics, and bulk operations
 */

@Repository
public interface FAQRepository extends JpaRepository<FAQ,Long>, IdentifiableRepository<FAQ> {
    Optional<FAQ> findByUuid(UUID uuid);
    Optional<FAQ> findBySlug(String slug);
    List<FAQ> findByCategoryOrderByDisplayOrderAsc(FAQCategory category);

    // ================================
    // PUBLIC QUERIES (for frontend users)
    // ================================


    @Query("""
        SELECT f
        FROM FAQ f
        JOIN FETCH f.category c
        WHERE f.isPublished = true
          AND c.isActive = true
          AND c.isVisible = true
        ORDER BY c.displayOrder ASC, f.displayOrder ASC
    """)
    List<FAQ> findAllPublishedWithCategory();

    /**
     * Find all published FAQs ordered by priority, display order, and creation date
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "AND f.category.isActive = true AND f.category.isVisible = true " +
            "ORDER BY f.priority DESC, f.displayOrder ASC, f.createdAt DESC")
    List<FAQ> findAllPublishedFAQs();

    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "AND f.category.uuid = :categoryUuid AND f.category.isActive = true " +
            "ORDER BY f.priority DESC, f.displayOrder ASC, f.createdAt DESC")
    List<FAQ> findPublishedFAQsByCategoryUuid(@Param("categoryUuid") UUID categoryUuid);


    // ================================
    // SEARCH FUNCTIONALITY
    // ================================


    // ================================
    // ADMIN QUERIES (for backend management)
    // ================================

    // ================================
    // ANALYTICS QUERIES
    // ================================

    // ================================
    // UPDATE OPERATIONS
    // ================================

    // ================================
    // COUNTING AND STATISTICS
    // ================================

    // ================================
    // BULK OPERATIONS
    // ================================

    @Modifying
    @Query("UPDATE FAQ f SET f.category = :newCategory, f.updatedBy = :updatedBy, f.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE f.category = :oldCategory")
    int moveFAQsToNewCategory(@Param("oldCategory") FAQCategory oldCategory,
                              @Param("newCategory") FAQCategory newCategory,
                              @Param("updatedBy") UUID updatedBy);

    // ================================
    // VALIDATION QUERIES
    // ================================

    /**
     * Check if slug exists in category (excluding specific UUID)
     */
    @Query("SELECT COUNT(f) > 0 FROM FAQ f " +
            "WHERE f.slug = :slug AND f.category = :category " +
            "AND (:excludeUuid IS NULL OR f.uuid != :excludeUuid)")
    boolean existsBySlugAndCategoryAndUuidNot(@Param("slug") String slug,
                                              @Param("category") FAQCategory category,
                                              @Param("excludeUuid") UUID excludeUuid);

    // ================================
    // MAINTENANCE QUERIES
    // ================================

    // FIX: Method for slug uniqueness checking
    boolean existsBySlugAndCategoryIdAndUuidNot(String slug, Long categoryId, UUID excludeUuid);

    @Query("SELECT COALESCE(MAX(f.displayOrder), 0) FROM FAQ f WHERE f.category.id = :categoryId")
    Optional<Integer> findMaxDisplayOrderByCategory(@Param("categoryId") Long categoryId);
}