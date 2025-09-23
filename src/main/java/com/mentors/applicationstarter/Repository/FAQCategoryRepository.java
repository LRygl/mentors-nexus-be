package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.DTO.FAQCategory.FAQCategoryPageResponseDTO;
import com.mentors.applicationstarter.Model.FAQCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FAQCategoryRepository extends JpaRepository<FAQCategory, Long> {

    // Basic finders
    Optional<FAQCategory> findByUuid(UUID uuid);
    Optional<FAQCategory> findBySlug(String slug);
    Optional<FAQCategory> findByName(String name);

    // Active categories
    @Query("SELECT c FROM FAQCategory c WHERE c.isActive = true ORDER BY c.displayOrder ASC, c.name ASC")
    List<FAQCategory> findAllActiveCategories();

    @Query("SELECT c FROM FAQCategory c WHERE c.isActive = true AND c.isVisible = true ORDER BY c.displayOrder ASC, c.name ASC")
    List<FAQCategory> findAllVisibleCategories();

    // Categories with published FAQs
    @Query("SELECT DISTINCT c FROM FAQCategory c " +
            "JOIN c.faqs f " +
            "WHERE c.isActive = true AND c.isVisible = true " +
            "AND f.isPublished = true AND f.status = 'PUBLISHED' " +
            "ORDER BY c.displayOrder ASC, c.name ASC")
    List<FAQCategory> findCategoriesWithPublishedFAQs();

    // Admin queries
    Page<FAQCategory> findAllByOrderByDisplayOrderAscNameAsc(Pageable pageable);

    @Query("SELECT c FROM FAQCategory c WHERE " +
            "(:isActive IS NULL OR c.isActive = :isActive) AND " +
            "(:searchTerm IS NULL OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<FAQCategory> findByFilters(@Param("isActive") Boolean isActive,
                                    @Param("searchTerm") String searchTerm,
                                    Pageable pageable);

    // Categories with FAQ counts
    @Query("SELECT c, COUNT(f.id) as faqCount, " +
            "SUM(CASE WHEN f.isPublished = true AND f.status = 'PUBLISHED' THEN 1 ELSE 0 END) as publishedCount " +
            "FROM FAQCategory c LEFT JOIN c.faqs f " +
            "WHERE c.isActive = true " +
            "GROUP BY c.id " +
            "ORDER BY c.displayOrder ASC, c.name ASC")
    List<Object[]> findCategoriesWithFAQCounts();

    // Validation queries
    @Query("SELECT COUNT(c) > 0 FROM FAQCategory c WHERE c.name = :name AND (:excludeUuid IS NULL OR c.uuid != :excludeUuid)")
    boolean existsByNameAndUuidNot(@Param("name") String name, @Param("excludeUuid") UUID excludeUuid);

    @Query("SELECT COUNT(c) > 0 FROM FAQCategory c WHERE c.slug = :slug AND (:excludeUuid IS NULL OR c.uuid != :excludeUuid)")
    boolean existsBySlugAndUuidNot(@Param("slug") String slug, @Param("excludeUuid") UUID excludeUuid);

    // Statistics
    @Query("SELECT COUNT(c) FROM FAQCategory c WHERE c.isActive = true")
    Long countActiveCategories();

    @Query("SELECT c FROM FAQCategory c WHERE c.isActive = true ORDER BY " +
            "(SELECT COUNT(f) FROM FAQ f WHERE f.category = c AND f.isPublished = true) DESC")
    List<FAQCategory> findCategoriesOrderByFAQCount(Pageable pageable);

    // Bulk operations
    @Query("SELECT c FROM FAQCategory c WHERE c.uuid IN :uuids")
    List<FAQCategory> findByUuidIn(@Param("uuids") List<UUID> uuids);
}
