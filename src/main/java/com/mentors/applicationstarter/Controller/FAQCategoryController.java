package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.Model.FAQCategory;
import com.mentors.applicationstarter.Service.FAQCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Public FAQ Category API Controller - accessible to all users
 * Provides read-only access to published FAQ categories
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/faq-category")
@RequiredArgsConstructor
@Tag(name = "FAQ Category Public API", description = "Public FAQ category operations for end users")
public class FAQCategoryController {

    private final FAQCategoryService faqCategoryService;

    @GetMapping
    @Operation(summary = "Get all visible categories", description = "Retrieves all visible FAQ categories ordered by display order")
    public ResponseEntity<List<FAQCategory>> getAllVisibleCategories() {
        log.debug("GET /api/v1/faq-category - Fetching all visible categories");
        List<FAQCategory> categories = faqCategoryService.getAllVisibleCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/with-faqs")
    @Operation(summary = "Get categories with published FAQs", description = "Retrieves categories that have published FAQs")
    public ResponseEntity<List<FAQCategory>> getCategoriesWithPublishedFAQs() {
        log.debug("GET /api/v1/faq-category/with-faqs - Fetching categories with published FAQs");
        List<FAQCategory> categories = faqCategoryService.getCategoriesWithPublishedFAQs();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/with-counts")
    @Operation(summary = "Get categories with FAQ counts", description = "Retrieves categories with their FAQ counts for analytics")
    public ResponseEntity<List<FAQCategory>> getCategoriesWithFAQCounts() {
        log.debug("GET /api/v1/faq-category/with-counts - Fetching categories with FAQ counts");
        List<FAQCategory> categories = faqCategoryService.getCategoriesWithFAQCounts();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get category by slug", description = "Retrieves a specific FAQ category by its URL slug")
    public ResponseEntity<FAQCategory> getCategoryBySlug(
            @Parameter(description = "Category URL slug") @PathVariable String slug) {
        log.debug("GET /api/v1/faq-category/slug/{} - Fetching category by slug", slug);
        Optional<FAQCategory> category = faqCategoryService.getCategoryBySlug(slug);

        return category.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get category by UUID", description = "Retrieves a specific FAQ category by its UUID")
    public ResponseEntity<FAQCategory> getCategoryByUuid(
            @Parameter(description = "Category UUID") @PathVariable UUID uuid) {
        log.debug("GET /api/v1/faq-category/{} - Fetching category by UUID", uuid);
        Optional<FAQCategory> category = faqCategoryService.getCategoryByUuid(uuid);

        return category.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/analytics/popular")
    @Operation(summary = "Get popular categories", description = "Retrieves most popular FAQ categories by FAQ count")
    public ResponseEntity<List<FAQCategory>> getPopularCategories(
            @Parameter(description = "Number of categories to return") @RequestParam(defaultValue = "5") int limit) {
        log.debug("GET /api/v1/faq-category/analytics/popular - Fetching {} popular categories", limit);
        List<FAQCategory> categories = faqCategoryService.getMostPopularCategories(limit);
        return ResponseEntity.ok(categories);
    }
}