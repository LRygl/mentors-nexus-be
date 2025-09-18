package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.FAQCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface FAQCategoryService {

    // Public API methods (for frontend users)
    List<FAQCategory> getAllVisibleCategories();
    List<FAQCategory> getCategoriesWithPublishedFAQs();
    Optional<FAQCategory> getCategoryBySlug(String slug);
    Optional<FAQCategory> getCategoryByUuid(UUID uuid);
    List<FAQCategory> getCategoriesWithFAQCounts();

    // Admin API methods (for backend management)
    Page<FAQCategory> getAllCategoriesForAdmin(Pageable pageable);
    Page<FAQCategory> getCategoriesByFilters(Boolean isActive, String searchTerm, Pageable pageable);
    FAQCategory createCategory(FAQCategory category);
    FAQCategory updateCategory(UUID uuid, FAQCategory category, UUID updatedBy);
    void deleteCategory(UUID uuid);
    FAQCategory activateCategory(UUID uuid, UUID updatedBy);
    FAQCategory deactivateCategory(UUID uuid, UUID updatedBy);
    void reorderCategories(List<UUID> orderedUuids);

    // Validation methods
    boolean isCategoryNameUnique(String name, UUID excludeUuid);
    boolean isCategorySlugUnique(String slug, UUID excludeUuid);

    // Analytics methods
    Long getActiveCategoryCount();
    List<FAQCategory> getMostPopularCategories(int limit);

    FAQCategory getCategoryById(Long faqCategoryId);
}
