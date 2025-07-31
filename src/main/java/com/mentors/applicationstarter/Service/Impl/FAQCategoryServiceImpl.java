package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceAlreadyExistsException;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.FAQCategory;
import com.mentors.applicationstarter.Repository.FAQCategoryRepository;
import com.mentors.applicationstarter.Service.FAQCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FAQCategoryServiceImpl implements FAQCategoryService {

    private final FAQCategoryRepository faqCategoryRepository;

    // Public API methods
    @Override
    @Transactional(readOnly = true)
    public List<FAQCategory> getAllVisibleCategories() {
        log.debug("Fetching all visible FAQ categories");
        return faqCategoryRepository.findAllVisibleCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FAQCategory> getCategoriesWithPublishedFAQs() {
        log.debug("Fetching categories with published FAQs");
        return faqCategoryRepository.findCategoriesWithPublishedFAQs();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FAQCategory> getCategoryBySlug(String slug) {
        log.debug("Fetching FAQ category by slug: {}", slug);
        return faqCategoryRepository.findBySlug(slug);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FAQCategory> getCategoryByUuid(UUID uuid) {
        log.debug("Fetching FAQ category by UUID: {}", uuid);
        return faqCategoryRepository.findByUuid(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FAQCategory> getCategoriesWithFAQCounts() {
        log.debug("Fetching categories with FAQ counts");
        List<Object[]> results = faqCategoryRepository.findCategoriesWithFAQCounts();

        return results.stream()
                .map(result -> {
                    FAQCategory category = (FAQCategory) result[0];
                    Long faqCount = (Long) result[1];
                    Long publishedCount = (Long) result[2];

                    category.setFaqCount(faqCount);
                    category.setPublishedFaqCount(publishedCount);

                    return category;
                })
                .collect(Collectors.toList());
    }

    // Admin API methods
    @Override
    @Transactional(readOnly = true)
    public Page<FAQCategory> getAllCategoriesForAdmin(Pageable pageable) {
        log.debug("Fetching all FAQ categories for admin with pagination");
        return faqCategoryRepository.findAllByOrderByDisplayOrderAscNameAsc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FAQCategory> getCategoriesByFilters(Boolean isActive, String searchTerm, Pageable pageable) {
        log.debug("Fetching FAQ categories with filters - active: {}, search: {}", isActive, searchTerm);
        return faqCategoryRepository.findByFilters(isActive, searchTerm, pageable);
    }

    @Override
    public FAQCategory createCategory(FAQCategory category, UUID createdBy) {
        log.debug("Creating new FAQ category: {}", category.getName());

        validateCategory(category, null);

        // Check for unique name and slug
        if (faqCategoryRepository.existsByNameAndUuidNot(category.getName(), null)) {
            throw new ResourceAlreadyExistsException(ErrorCodes.FAQ_CATEGORY_NAME_EXISTS, category.getName());
        }

        String slug = generateSlug(category.getName());
        if (faqCategoryRepository.existsBySlugAndUuidNot(slug, null)) {
            throw new ResourceAlreadyExistsException(ErrorCodes.FAQ_CATEGORY_SLUG_EXISTS, slug);
        }

        FAQCategory newCategory = FAQCategory.builder()
                .uuid(UUID.randomUUID())
                .name(category.getName().trim())
                .description(category.getDescription() != null ? category.getDescription().trim() : null)
                .slug(slug)
                .iconClass(category.getIconClass())
                .colorCode(category.getColorCode())
                .displayOrder(category.getDisplayOrder() != null ? category.getDisplayOrder() : 0)
                .isActive(category.getIsActive() != null ? category.getIsActive() : true)
                .isVisible(category.getIsVisible() != null ? category.getIsVisible() : true)
                .metaDescription(category.getMetaDescription())
                .metaKeywords(category.getMetaKeywords())
                .createdBy(createdBy)
                .build();

        return faqCategoryRepository.save(newCategory);
    }

    @Override
    public FAQCategory updateCategory(UUID uuid, FAQCategory category, UUID updatedBy) {
        log.debug("Updating FAQ category: {}", uuid);

        FAQCategory existingCategory = faqCategoryRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_CATEGORY_NOT_FOUND));

        validateCategory(category, uuid);

        // Check for unique name and slug (excluding current category)
        if (faqCategoryRepository.existsByNameAndUuidNot(category.getName(), uuid)) {
            throw new ResourceAlreadyExistsException(ErrorCodes.FAQ_CATEGORY_NAME_EXISTS, category.getName());
        }

        String slug = generateSlug(category.getName());
        if (faqCategoryRepository.existsBySlugAndUuidNot(slug, uuid)) {
            throw new ResourceAlreadyExistsException(ErrorCodes.FAQ_CATEGORY_SLUG_EXISTS, slug);
        }

        existingCategory.setName(category.getName().trim());
        existingCategory.setDescription(category.getDescription() != null ? category.getDescription().trim() : null);
        existingCategory.setSlug(slug);
        existingCategory.setIconClass(category.getIconClass());
        existingCategory.setColorCode(category.getColorCode());
        existingCategory.setDisplayOrder(category.getDisplayOrder());
        existingCategory.setIsVisible(category.getIsVisible());
        existingCategory.setMetaDescription(category.getMetaDescription());
        existingCategory.setMetaKeywords(category.getMetaKeywords());
        existingCategory.setUpdatedBy(updatedBy);

        return faqCategoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(UUID uuid) {
        log.debug("Deleting FAQ category: {}", uuid);
        FAQCategory category = faqCategoryRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_CATEGORY_NOT_FOUND));

        // Check if category has FAQs
        if (!category.getFaqs().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with existing FAQs. Move or delete FAQs first.");
        }

        faqCategoryRepository.delete(category);
    }

    @Override
    public FAQCategory activateCategory(UUID uuid, UUID updatedBy) {
        log.debug("Activating FAQ category: {}", uuid);
        FAQCategory category = faqCategoryRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_CATEGORY_NOT_FOUND));

        category.setIsActive(true);
        category.setUpdatedBy(updatedBy);

        return faqCategoryRepository.save(category);
    }

    @Override
    public FAQCategory deactivateCategory(UUID uuid, UUID updatedBy) {
        log.debug("Deactivating FAQ category: {}", uuid);
        FAQCategory category = faqCategoryRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_CATEGORY_NOT_FOUND));

        category.setIsActive(false);
        category.setUpdatedBy(updatedBy);

        return faqCategoryRepository.save(category);
    }

    @Override
    public void reorderCategories(List<UUID> orderedUuids) {
        log.debug("Reordering {} FAQ categories", orderedUuids.size());

        AtomicInteger order = new AtomicInteger(1);
        orderedUuids.forEach(uuid -> {
            FAQCategory category = faqCategoryRepository.findByUuid(uuid)
                    .orElse(null);
            if (category != null) {
                category.setDisplayOrder(order.getAndIncrement());
                faqCategoryRepository.save(category);
            }
        });
    }

    // Validation methods
    @Override
    @Transactional(readOnly = true)
    public boolean isCategoryNameUnique(String name, UUID excludeUuid) {
        return !faqCategoryRepository.existsByNameAndUuidNot(name, excludeUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCategorySlugUnique(String slug, UUID excludeUuid) {
        return !faqCategoryRepository.existsBySlugAndUuidNot(slug, excludeUuid);
    }

    // Analytics methods
    @Override
    @Transactional(readOnly = true)
    public Long getActiveCategoryCount() {
        return faqCategoryRepository.countActiveCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FAQCategory> getMostPopularCategories(int limit) {
        log.debug("Fetching {} most popular FAQ categories", limit);
        return faqCategoryRepository.findCategoriesOrderByFAQCount(PageRequest.of(0, limit));
    }

    // Helper methods
    private void validateCategory(FAQCategory category, UUID excludeUuid) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        if (category.getName().length() > 100) {
            throw new IllegalArgumentException("Category name too long (max 100 characters)");
        }

        if (category.getDescription() != null && category.getDescription().length() > 500) {
            throw new IllegalArgumentException("Category description too long (max 500 characters)");
        }

        if (category.getColorCode() != null && !category.getColorCode().matches("^#[0-9A-Fa-f]{6}$")) {
            throw new IllegalArgumentException("Invalid color code format. Use hex format like #FF0000");
        }
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "") // Remove special characters
                .replaceAll("\\s+", "-") // Replace spaces with hyphens
                .replaceAll("-+", "-") // Replace multiple hyphens with single
                .replaceAll("^-|-$", ""); // Remove leading/trailing hyphens
    }
}
