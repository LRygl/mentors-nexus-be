package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.FAQCategory.FAQCategoryPublicResponseDTO;
import com.mentors.applicationstarter.DTO.FAQCategory.FAQCategoryResponseDTO;
import com.mentors.applicationstarter.Model.FAQCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface FAQCategoryService {

    // Public API methods (for frontend users)
    List<FAQCategoryPublicResponseDTO> getAllVisibleCategories();
    List<FAQCategory> getCategoriesWithPublishedFAQs();
    FAQCategory getCategoryByUuid(UUID uuid);

    // Admin API methods (for backend management)
    Page<FAQCategoryResponseDTO> getAllCategoriesForAdmin(Pageable pageable);
    Page<FAQCategoryResponseDTO> getCategoriesByFilters(Boolean isActive, String searchTerm, Pageable pageable);
    FAQCategoryResponseDTO createCategory(FAQCategory category);
    FAQCategoryResponseDTO updateCategory(UUID uuid, FAQCategory category, UUID updatedBy);
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

    FAQCategory getFAQCategoryEntityById(Long faqCategoryId);
    FAQCategoryResponseDTO getFAQCategoryById(Long faqCategoryId);
}
