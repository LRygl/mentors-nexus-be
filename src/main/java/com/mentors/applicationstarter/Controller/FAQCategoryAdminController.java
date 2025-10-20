package com.mentors.applicationstarter.Controller;


import com.mentors.applicationstarter.DTO.CategoryStats;
import com.mentors.applicationstarter.DTO.FAQCategory.FAQCategoryResponseDTO;
import com.mentors.applicationstarter.Exception.ResourceAlreadyExistsException;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.FAQCategory;
import com.mentors.applicationstarter.Service.FAQCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Admin FAQ Category API Controller - restricted to admin users
 * Provides full CRUD operations for FAQ category management
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/faq-category")
@RequiredArgsConstructor
@Tag(name = "FAQ Category Admin API", description = "Admin FAQ category operations for content management")
public class FAQCategoryAdminController {

    private final FAQCategoryService faqCategoryService;

    @GetMapping
    @Operation(summary = "Get all categories for admin", description = "Retrieves all FAQ categories with pagination for admin interface")
    public ResponseEntity<Page<FAQCategoryResponseDTO>> getAllCategoriesForAdmin(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Active status filter") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);

        Page<FAQCategoryResponseDTO> categories;
        if (isActive != null || (search != null && !search.trim().isEmpty())) {
            categories = faqCategoryService.getCategoriesByFilters(isActive, search, pageable);
        } else {
            categories = faqCategoryService.getAllCategoriesForAdmin(pageable);
        }

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{faqCategoryId}")
    public ResponseEntity<FAQCategoryResponseDTO> getFAQCategoryById(@PathVariable Long faqCategoryId) {
        return ResponseEntity.ok(faqCategoryService.getFAQCategoryById(faqCategoryId));
    }

    @PostMapping
    @Operation(summary = "Create new category", description = "Creates a new FAQ category")
    public ResponseEntity<FAQCategoryResponseDTO> createCategory(
            @Parameter(description = "Category data") @RequestBody FAQCategory category) {

            FAQCategoryResponseDTO createdCategory = faqCategoryService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update category", description = "Updates an existing FAQ category")
    public ResponseEntity<FAQCategoryResponseDTO> updateCategory(
            @Parameter(description = "Category UUID") @PathVariable UUID uuid,
            @Parameter(description = "Updated category data") @RequestBody FAQCategory category,
            @Parameter(description = "Admin user UUID") @RequestHeader("X-User-UUID") UUID adminUuid) {

            FAQCategoryResponseDTO updatedCategory = faqCategoryService.updateCategory(uuid, category, adminUuid);
            return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete category", description = "Deletes an FAQ category")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category UUID") @PathVariable UUID uuid) {

        log.debug("DELETE /api/v1/admin/faq-category/{} - Deleting category", uuid);

        try {
            faqCategoryService.deleteCategory(uuid);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            log.warn("Category deletion failed - not found: {}", uuid);
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.warn("Category deletion failed - has dependencies: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PatchMapping("/{uuid}/activate")
    @Operation(summary = "Activate category", description = "Activates an FAQ category")
    public ResponseEntity<FAQCategory> activateCategory(
            @Parameter(description = "Category UUID") @PathVariable UUID uuid,
            @Parameter(description = "Admin user UUID") @RequestHeader("X-User-UUID") UUID adminUuid) {

        log.debug("PATCH /api/v1/admin/faq-category/{}/activate - Activating category", uuid);

        try {
            FAQCategory category = faqCategoryService.activateCategory(uuid, adminUuid);
            return ResponseEntity.ok(category);
        } catch (ResourceNotFoundException e) {
            log.warn("Category activation failed - not found: {}", uuid);
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{uuid}/deactivate")
    @Operation(summary = "Deactivate category", description = "Deactivates an FAQ category")
    public ResponseEntity<FAQCategory> deactivateCategory(
            @Parameter(description = "Category UUID") @PathVariable UUID uuid,
            @Parameter(description = "Admin user UUID") @RequestHeader("X-User-UUID") UUID adminUuid) {

        log.debug("PATCH /api/v1/admin/faq-category/{}/deactivate - Deactivating category", uuid);

        try {
            FAQCategory category = faqCategoryService.deactivateCategory(uuid, adminUuid);
            return ResponseEntity.ok(category);
        } catch (ResourceNotFoundException e) {
            log.warn("Category deactivation failed - not found: {}", uuid);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/reorder")
    @Operation(summary = "Reorder categories", description = "Updates the display order of FAQ categories")
    public ResponseEntity<Void> reorderCategories(
            @Parameter(description = "Ordered list of category UUIDs") @RequestBody List<UUID> orderedUuids) {

        log.debug("PUT /api/v1/admin/faq-category/reorder - Reordering {} categories", orderedUuids.size());

        faqCategoryService.reorderCategories(orderedUuids);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate/name")
    @Operation(summary = "Validate category name", description = "Checks if a category name is unique")
    public ResponseEntity<Boolean> validateCategoryName(
            @Parameter(description = "Category name to validate") @RequestParam String name,
            @Parameter(description = "UUID to exclude from validation") @RequestParam(required = false) UUID excludeUuid) {

        log.debug("GET /api/v1/admin/faq-category/validate/name - Validating name: {}", name);

        boolean isUnique = faqCategoryService.isCategoryNameUnique(name, excludeUuid);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/validate/slug")
    @Operation(summary = "Validate category slug", description = "Checks if a category slug is unique")
    public ResponseEntity<Boolean> validateCategorySlug(
            @Parameter(description = "Category slug to validate") @RequestParam String slug,
            @Parameter(description = "UUID to exclude from validation") @RequestParam(required = false) UUID excludeUuid) {

        log.debug("GET /api/v1/admin/faq-category/validate/slug - Validating slug: {}", slug);

        boolean isUnique = faqCategoryService.isCategorySlugUnique(slug, excludeUuid);
        return ResponseEntity.ok(isUnique);
    }

    @GetMapping("/analytics/stats")
    @Operation(summary = "Get category statistics", description = "Retrieves FAQ category statistics for dashboard")
    public ResponseEntity<CategoryStats> getCategoryStats() {
        log.debug("GET /api/v1/admin/faq-category/analytics/stats - Fetching category statistics");

        try {
            Long activeCategoryCount = faqCategoryService.getActiveCategoryCount();
            List<FAQCategory> popularCategories = faqCategoryService.getMostPopularCategories(5);

            CategoryStats stats = CategoryStats.builder()
                    .totalActiveCategories(activeCategoryCount)
                    .mostPopularCategories(popularCategories)
                    .build();

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Failed to fetch category statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
