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


    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "AND f.category.slug = :categorySlug AND f.category.isActive = true " +
            "ORDER BY f.priority DESC, f.displayOrder ASC, f.createdAt DESC")
    List<FAQ> findPublishedFAQsByCategorySlug(@Param("categorySlug") String categorySlug);

    /**
     * Find featured FAQs for homepage display
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "AND f.isFeatured = true AND f.category.isActive = true " +
            "ORDER BY f.priority DESC, f.displayOrder ASC, f.viewCount DESC")
    List<FAQ> findFeaturedFAQs();

    // ================================
    // SEARCH FUNCTIONALITY
    // ================================



    /**
     * Advanced search with relevance ranking using native SQL for better performance
     */
    @Query(value = "SELECT f.*, " +
            "CASE " +
            "  WHEN LOWER(f.question) LIKE LOWER(CONCAT('%', :searchTerm, '%')) THEN 10 " +
            "  WHEN LOWER(f.answer) LIKE LOWER(CONCAT('%', :searchTerm, '%')) THEN 7 " +
            "  WHEN LOWER(f.search_keywords) LIKE LOWER(CONCAT('%', :searchTerm, '%')) THEN 5 " +
            "  WHEN LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) THEN 3 " +
            "  ELSE 1 " +
            "END + " +
            "CASE " +
            "  WHEN f.is_featured = true THEN 5 " +
            "  ELSE 0 " +
            "END + " +
            "CASE " +
            "  WHEN f.priority = 'URGENT' THEN 8 " +
            "  WHEN f.priority = 'HIGH' THEN 6 " +
            "  WHEN f.priority = 'NORMAL' THEN 4 " +
            "  WHEN f.priority = 'LOW' THEN 2 " +
            "  ELSE 0 " +
            "END as relevance_score " +
            "FROM faq f " +
            "JOIN faq_category c ON f.category_id = c.id " +
            "WHERE f.is_published = true AND f.status = 'PUBLISHED' " +
            "AND c.is_active = true AND c.is_visible = true " +
            "AND (LOWER(f.question) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(f.answer) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(f.search_keywords) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY relevance_score DESC, f.view_count DESC, f.display_order ASC",
            nativeQuery = true)
    List<FAQ> searchWithRelevanceRanking(@Param("searchTerm") String searchTerm);

    // ================================
    // ADMIN QUERIES (for backend management)
    // ================================

    /**
     * Find FAQs by category UUID
     */
    Page<FAQ> findByCategoryUuidOrderByDisplayOrderAsc(UUID categoryUuid, Pageable pageable);


    // ================================
    // ANALYTICS QUERIES
    // ================================

    /**
     * Find most viewed FAQs (published only)
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "ORDER BY f.viewCount DESC")
    List<FAQ> findMostViewedFAQs(Pageable pageable);


    /**
     * Find most helpful FAQs based on votes
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "ORDER BY f.helpfulVotes DESC, (f.helpfulVotes * 1.0 / NULLIF(f.helpfulVotes + f.notHelpfulVotes, 0)) DESC")
    List<FAQ> findMostHelpfulFAQs(Pageable pageable);


    // ================================
    // UPDATE OPERATIONS
    // ================================

    /**
     * Increment view count atomically
     */
    @Modifying
    @Query("UPDATE FAQ f SET f.viewCount = f.viewCount + 1 WHERE f.uuid = :uuid")
    int incrementViewCount(@Param("uuid") UUID uuid);

    /**
     * Increment helpful votes
     */
    @Modifying
    @Query("UPDATE FAQ f SET f.helpfulVotes = f.helpfulVotes + 1 WHERE f.uuid = :uuid")
    int incrementHelpfulVotes(@Param("uuid") UUID uuid);

    /**
     * Increment not helpful votes
     */
    @Modifying
    @Query("UPDATE FAQ f SET f.notHelpfulVotes = f.notHelpfulVotes + 1 WHERE f.uuid = :uuid")
    int incrementNotHelpfulVotes(@Param("uuid") UUID uuid);



    // ================================
    // COUNTING AND STATISTICS
    // ================================

    // ================================
    // BULK OPERATIONS
    // ================================

    /**
     * Find FAQs by UUID list
     */
    @Query("SELECT f FROM FAQ f WHERE f.uuid IN :uuids ORDER BY f.displayOrder ASC")
    List<FAQ> findByUuidIn(@Param("uuids") List<UUID> uuids);

    /**
     * Move FAQs from one category to another
     */
    @Modifying
    @Query("UPDATE FAQ f SET f.category = :newCategory, f.updatedBy = :updatedBy, f.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE f.category = :oldCategory")
    int moveFAQsToNewCategory(@Param("oldCategory") FAQCategory oldCategory,
                              @Param("newCategory") FAQCategory newCategory,
                              @Param("updatedBy") UUID updatedBy);

    /**
     * Delete FAQs by category (cascade delete protection)
     */
    @Modifying
    @Query("DELETE FROM FAQ f WHERE f.category = :category")
    int deleteFAQsByCategory(@Param("category") FAQCategory category);

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

    // FIX: Alternative approach - Use native query with explicit type casting
    @Query(value = """
        SELECT f.* FROM faq f 
        JOIN faqcategory c ON c.id = f.category_id 
        WHERE (:status IS NULL OR f.status = CAST(:status AS varchar))
        AND (:categoryUuid IS NULL OR c.uuid = CAST(:categoryUuid AS uuid))
        AND (:priority IS NULL OR f.priority = CAST(:priority AS varchar))
        AND (:searchTerm IS NULL OR 
             LOWER(f.question::TEXT) ILIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
             LOWER(f.answer::TEXT) ILIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
             LOWER(f.search_keywords::TEXT) ILIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
             LOWER(c.name::TEXT) ILIKE LOWER(CONCAT('%', :searchTerm, '%')))
        ORDER BY f.created_at DESC
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<FAQ> findByFiltersNative(
            @Param("status") String status,
            @Param("categoryUuid") String categoryUuid,
            @Param("priority") String priority,
            @Param("searchTerm") String searchTerm,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    // FIX: Count query for pagination
    @Query(value = """
        SELECT COUNT(*) FROM faq f 
        JOIN faqcategory c ON c.id = f.category_id 
        WHERE (:status IS NULL OR f.status = CAST(:status AS varchar))
        AND (:categoryUuid IS NULL OR c.uuid = CAST(:categoryUuid AS uuid))
        AND (:priority IS NULL OR f.priority = CAST(:priority AS varchar))
        AND (:searchTerm IS NULL OR 
             LOWER(f.question::TEXT) ILIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
             LOWER(f.answer::TEXT) ILIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
             LOWER(f.search_keywords::TEXT) ILIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
             LOWER(c.name::TEXT) ILIKE LOWER(CONCAT('%', :searchTerm, '%')))
        """, nativeQuery = true)
    long countByFiltersNative(
            @Param("status") String status,
            @Param("categoryUuid") String categoryUuid,
            @Param("priority") String priority,
            @Param("searchTerm") String searchTerm
    );

    @Query("SELECT COALESCE(MAX(f.displayOrder), 0) FROM FAQ f WHERE f.category.id = :categoryId")
    Optional<Integer> findMaxDisplayOrderByCategory(@Param("categoryId") Long categoryId);
}