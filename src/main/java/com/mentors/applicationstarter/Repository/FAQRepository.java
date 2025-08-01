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
public interface FAQRepository extends JpaRepository<FAQ,Long> {

    // ================================
    // BASIC FINDERS
    // ================================

    /**
     * Find FAQ by UUID
     */
    Optional<FAQ> findByUuid(UUID uuid);

    /**
     * Find FAQ by slug (for SEO-friendly URLs)
     */
    Optional<FAQ> findBySlug(String slug);

    /**
     * Find FAQ by slug and category (ensures uniqueness within category)
     */
    Optional<FAQ> findBySlugAndCategory(String slug, FAQCategory category);

    /**
     * Check if FAQ exists by UUID
     */
    boolean existsByUuid(UUID uuid);

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

    /**
     * Find published FAQs by category entity
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "AND f.category = :category AND f.category.isActive = true " +
            "ORDER BY f.priority DESC, f.displayOrder ASC, f.createdAt DESC")
    List<FAQ> findPublishedFAQsByCategory(@Param("category") FAQCategory category);

    /**
     * Find published FAQs by category UUID
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "AND f.category.uuid = :categoryUuid AND f.category.isActive = true " +
            "ORDER BY f.priority DESC, f.displayOrder ASC, f.createdAt DESC")
    List<FAQ> findPublishedFAQsByCategoryUuid(@Param("categoryUuid") UUID categoryUuid);

    /**
     * Find published FAQs by category slug (SEO-friendly)
     */
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

    /**
     * Find featured FAQs with limit
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "AND f.isFeatured = true AND f.category.isActive = true " +
            "ORDER BY f.priority DESC, f.displayOrder ASC, f.viewCount DESC")
    List<FAQ> findFeaturedFAQs(Pageable pageable);

    // ================================
    // SEARCH FUNCTIONALITY
    // ================================

    /**
     * Basic search across published FAQs
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "AND f.category.isActive = true AND f.category.isVisible = true " +
            "AND (LOWER(f.question) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(f.answer) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(f.searchKeywords) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(f.category.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY f.priority DESC, f.viewCount DESC, f.displayOrder ASC")
    List<FAQ> searchPublishedFAQs(@Param("searchTerm") String searchTerm);

    /**
     * Search FAQs within a specific category
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "AND f.category = :category AND f.category.isActive = true " +
            "AND (LOWER(f.question) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(f.answer) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(f.searchKeywords) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY f.priority DESC, f.viewCount DESC, f.displayOrder ASC")
    List<FAQ> searchPublishedFAQsInCategory(@Param("searchTerm") String searchTerm,
                                            @Param("category") FAQCategory category);

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
     * Find all FAQs for admin with pagination
     */
    Page<FAQ> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find FAQs by status
     */
    Page<FAQ> findByStatusOrderByCreatedAtDesc(FAQStatus status, Pageable pageable);

    /**
     * Find FAQs by category
     */
    Page<FAQ> findByCategoryOrderByDisplayOrderAsc(FAQCategory category, Pageable pageable);

    /**
     * Find FAQs by category UUID
     */
    Page<FAQ> findByCategoryUuidOrderByDisplayOrderAsc(UUID categoryUuid, Pageable pageable);

    /**
     * Find FAQs by priority
     */
    Page<FAQ> findByPriorityOrderByCreatedAtDesc(FAQPriority priority, Pageable pageable);

    /**
     * Complex filtering for admin interface
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE (:status IS NULL OR f.status = :status) " +
            "AND (:categoryUuid IS NULL OR f.category.uuid = :categoryUuid) " +
            "AND (:priority IS NULL OR f.priority = :priority) " +
            "AND (:searchTerm IS NULL OR " +
            "     LOWER(f.question) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(f.answer) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(f.searchKeywords) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(f.category.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY f.createdAt DESC")
    Page<FAQ> findByFilters(@Param("status") FAQStatus status,
                            @Param("categoryUuid") UUID categoryUuid,
                            @Param("priority") FAQPriority priority,
                            @Param("searchTerm") String searchTerm,
                            Pageable pageable);

    /**
     * Advanced filtering for admin interface with published and featured status
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE (:status IS NULL OR f.status = :status) " +
            "AND (:categoryUuid IS NULL OR f.category.uuid = :categoryUuid) " +
            "AND (:priority IS NULL OR f.priority = :priority) " +
            "AND (:isPublished IS NULL OR f.isPublished = :isPublished) " +
            "AND (:isFeatured IS NULL OR f.isFeatured = :isFeatured) " +
            "AND (:searchTerm IS NULL OR " +
            "     LOWER(f.question) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(f.answer) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(f.searchKeywords) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "     LOWER(f.category.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY f.createdAt DESC")
    Page<FAQ> findByFiltersAdvanced(@Param("status") FAQStatus status,
                                    @Param("categoryUuid") UUID categoryUuid,
                                    @Param("priority") FAQPriority priority,
                                    @Param("isPublished") Boolean isPublished,
                                    @Param("isFeatured") Boolean isFeatured,
                                    @Param("searchTerm") String searchTerm,
                                    Pageable pageable);

    /**
     * Find FAQs created between dates
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY f.createdAt DESC")
    List<FAQ> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * Find FAQs updated between dates
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.updatedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY f.updatedAt DESC")
    List<FAQ> findByUpdatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

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
     * Find most viewed FAQs in a specific category
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "AND f.category = :category " +
            "ORDER BY f.viewCount DESC")
    List<FAQ> findMostViewedFAQsInCategory(@Param("category") FAQCategory category, Pageable pageable);

    /**
     * Find most helpful FAQs based on votes
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "ORDER BY f.helpfulVotes DESC, (f.helpfulVotes * 1.0 / NULLIF(f.helpfulVotes + f.notHelpfulVotes, 0)) DESC")
    List<FAQ> findMostHelpfulFAQs(Pageable pageable);

    /**
     * Find FAQs with highest helpfulness ratio
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "AND (f.helpfulVotes + f.notHelpfulVotes) >= :minVotes " +
            "ORDER BY (f.helpfulVotes * 1.0 / (f.helpfulVotes + f.notHelpfulVotes)) DESC, f.helpfulVotes DESC")
    List<FAQ> findMostHelpfulFAQsByRatio(@Param("minVotes") int minVotes, Pageable pageable);

    /**
     * Find trending FAQs (high recent view count)
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "AND f.updatedAt >= :since " +
            "ORDER BY f.viewCount DESC, f.helpfulVotes DESC")
    List<FAQ> findTrendingFAQs(@Param("since") LocalDateTime since, Pageable pageable);

    /**
     * Find recently created published FAQs
     */
    @Query("SELECT f FROM FAQ f " +
            "WHERE f.isPublished = true AND f.status = 'PUBLISHED' " +
            "AND f.createdAt >= :since " +
            "ORDER BY f.createdAt DESC")
    List<FAQ> findRecentlyPublishedFAQs(@Param("since") LocalDateTime since, Pageable pageable);

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

    /**
     * Bulk update status
     */
    @Modifying
    @Query("UPDATE FAQ f SET f.status = :newStatus, f.updatedBy = :updatedBy, f.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE f.uuid IN :uuids")
    int updateStatusBulk(@Param("uuids") List<UUID> uuids,
                         @Param("newStatus") FAQStatus newStatus,
                         @Param("updatedBy") UUID updatedBy);

    /**
     * Bulk update published status
     */
    @Modifying
    @Query("UPDATE FAQ f SET f.isPublished = :isPublished, f.updatedBy = :updatedBy, f.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE f.uuid IN :uuids")
    int updatePublishedStatusBulk(@Param("uuids") List<UUID> uuids,
                                  @Param("isPublished") Boolean isPublished,
                                  @Param("updatedBy") UUID updatedBy);

    /**
     * Bulk update featured status
     */
    @Modifying
    @Query("UPDATE FAQ f SET f.isFeatured = :isFeatured, f.updatedBy = :updatedBy, f.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE f.uuid IN :uuids")
    int updateFeaturedStatusBulk(@Param("uuids") List<UUID> uuids,
                                 @Param("isFeatured") Boolean isFeatured,
                                 @Param("updatedBy") UUID updatedBy);

    /**
     * Reset view counts (for maintenance)
     */
    @Modifying
    @Query("UPDATE FAQ f SET f.viewCount = 0")
    int resetAllViewCounts();

    // ================================
    // COUNTING AND STATISTICS
    // ================================

    /**
     * Count FAQs by status
     */
    @Query("SELECT COUNT(f) FROM FAQ f WHERE f.status = :status")
    Long countByStatus(@Param("status") FAQStatus status);

    /**
     * Count FAQs by priority
     */
    @Query("SELECT COUNT(f) FROM FAQ f WHERE f.priority = :priority")
    Long countByPriority(@Param("priority") FAQPriority priority);

    /**
     * Count published FAQs
     */
    @Query("SELECT COUNT(f) FROM FAQ f WHERE f.isPublished = true AND f.status = 'PUBLISHED'")
    Long countPublishedFAQs();

    /**
     * Count featured FAQs
     */
    @Query("SELECT COUNT(f) FROM FAQ f WHERE f.isFeatured = true AND f.isPublished = true")
    Long countFeaturedFAQs();

    /**
     * Count FAQs by category
     */
    @Query("SELECT COUNT(f) FROM FAQ f WHERE f.category = :category")
    Long countFAQsByCategory(@Param("category") FAQCategory category);

    /**
     * Count published FAQs by category
     */
    @Query("SELECT COUNT(f) FROM FAQ f " +
            "WHERE f.category = :category AND f.isPublished = true AND f.status = 'PUBLISHED'")
    Long countPublishedFAQsByCategory(@Param("category") FAQCategory category);

    /**
     * Count FAQs by category UUID
     */
    @Query("SELECT COUNT(f) FROM FAQ f WHERE f.category.uuid = :categoryUuid")
    Long countFAQsByCategoryUuid(@Param("categoryUuid") UUID categoryUuid);

    /**
     * Count published FAQs by category UUID
     */
    @Query("SELECT COUNT(f) FROM FAQ f " +
            "WHERE f.category.uuid = :categoryUuid AND f.isPublished = true AND f.status = 'PUBLISHED'")
    Long countPublishedFAQsByCategoryUuid(@Param("categoryUuid") UUID categoryUuid);

    /**
     * Get total view count across all FAQs
     */
    @Query("SELECT COALESCE(SUM(f.viewCount), 0) FROM FAQ f")
    Long getTotalViewCount();

    /**
     * Get total helpful votes across all FAQs
     */
    @Query("SELECT COALESCE(SUM(f.helpfulVotes), 0) FROM FAQ f")
    Long getTotalHelpfulVotes();

    /**
     * Get total not helpful votes across all FAQs
     */
    @Query("SELECT COALESCE(SUM(f.notHelpfulVotes), 0) FROM FAQ f")
    Long getTotalNotHelpfulVotes();

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

    /**
     * Check if question exists in category (duplicate detection)
     */
    @Query("SELECT COUNT(f) > 0 FROM FAQ f " +
            "WHERE LOWER(f.question) = LOWER(:question) AND f.category = :category " +
            "AND (:excludeUuid IS NULL OR f.uuid != :excludeUuid)")
    boolean existsByQuestionAndCategoryAndUuidNot(@Param("question") String question,
                                                  @Param("category") FAQCategory category,
                                                  @Param("excludeUuid") UUID excludeUuid);

    // ================================
    // MAINTENANCE QUERIES
    // ================================

    /**
     * Find orphaned FAQs (categories that don't exist)
     */
    @Query("SELECT f FROM FAQ f WHERE f.category IS NULL")
    List<FAQ> findOrphanedFAQs();

    /**
     * Find FAQs with no views (for cleanup)
     */
    @Query("SELECT f FROM FAQ f WHERE f.viewCount = 0 AND f.createdAt < :beforeDate")
    List<FAQ> findFAQsWithNoViews(@Param("beforeDate") LocalDateTime beforeDate);

    /**
     * Find duplicate slugs within categories (data integrity check)
     */
    @Query(value = "SELECT f1.* FROM faq f1 " +
            "INNER JOIN faq f2 ON f1.slug = f2.slug AND f1.category_id = f2.category_id AND f1.id != f2.id",
            nativeQuery = true)
    List<FAQ> findDuplicateSlugFAQs();

    /**
     * Archive old FAQs automatically
     */
    @Modifying
    @Query("UPDATE FAQ f SET f.status = 'ARCHIVED', f.isPublished = false, f.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE f.status = 'PUBLISHED' AND f.viewCount < :minViews AND f.createdAt < :beforeDate")
    int archiveOldUnpopularFAQs(@Param("minViews") Long minViews, @Param("beforeDate") LocalDateTime beforeDate);

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

}