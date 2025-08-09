package com.mentors.applicationstarter.Controller;


import com.mentors.applicationstarter.DTO.FAQStats;
import com.mentors.applicationstarter.Enum.FAQPriority;
import com.mentors.applicationstarter.Enum.FAQStatus;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.FAQ;
import com.mentors.applicationstarter.Service.FAQService;
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
 * Admin FAQ API Controller - restricted to admin users
 * Provides full CRUD operations for FAQ management
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/faq")
@RequiredArgsConstructor
@Tag(name = "FAQ Admin API", description = "Admin FAQ operations for content management")
public class FAQAdminController {

    private final FAQService faqService;

    @GetMapping
    @Operation(summary = "Get all FAQs for admin", description = "Retrieves all FAQs with pagination and filtering for admin interface")
    public ResponseEntity<Page<FAQ>> getAllFAQsForAdmin(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Status filter") @RequestParam(required = false) FAQStatus status,
            @Parameter(description = "Category UUID filter") @RequestParam(required = false) UUID categoryUuid,
            @Parameter(description = "Priority filter") @RequestParam(required = false) FAQPriority priority,
            @Parameter(description = "Search term") @RequestParam(required = false) String search) {

        log.debug("GET /api/v1/admin/faq - Fetching FAQs for admin");
        Pageable pageable = PageRequest.of(page, size);

        Page<FAQ> faqs = faqService.getFAQsByFilters(status, categoryUuid, priority, search, pageable);
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/category/{categoryUuid}")
    @Operation(summary = "Get FAQs by category for admin", description = "Retrieves all FAQs in a specific category for admin")
    public ResponseEntity<Page<FAQ>> getFAQsByCategoryForAdmin(
            @Parameter(description = "Category UUID") @PathVariable UUID categoryUuid,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {

        log.debug("GET /api/v1/admin/faq/category/{} - Fetching FAQs by category for admin", categoryUuid);
        Pageable pageable = PageRequest.of(page, size);
        Page<FAQ> faqs = faqService.getFAQsByCategoryForAdmin(categoryUuid, pageable);
        return ResponseEntity.ok(faqs);
    }

    @PostMapping
    @Operation(summary = "Create new FAQ", description = "Creates a new FAQ")
    public ResponseEntity<FAQ> createFAQ(
            @Parameter(description = "FAQ data") @RequestBody FAQ faq,
            @Parameter(description = "Admin user UUID") @RequestHeader("X-User-UUID") UUID adminUuid) {

        log.debug("POST /api/v1/admin/faq - Creating new FAQ: {}", faq.getQuestion());

        try {
            FAQ createdFAQ = faqService.createFAQ(faq, adminUuid);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFAQ);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid FAQ data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update FAQ", description = "Updates an existing FAQ")
    public ResponseEntity<FAQ> updateFAQ(
            @Parameter(description = "FAQ UUID") @PathVariable UUID uuid,
            @Parameter(description = "Updated FAQ data") @RequestBody FAQ faq,
            @Parameter(description = "Admin user UUID") @RequestHeader("X-User-UUID") UUID adminUuid) {

        log.debug("PUT /api/v1/admin/faq/{} - Updating FAQ", uuid);

        try {
            FAQ updatedFAQ = faqService.updateFAQ(uuid, faq, adminUuid);
            return ResponseEntity.ok(updatedFAQ);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.warn("Invalid FAQ data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete FAQ", description = "Deletes an FAQ")
    public ResponseEntity<Void> deleteFAQ(
            @Parameter(description = "FAQ UUID") @PathVariable UUID uuid) {

        log.debug("DELETE /api/v1/admin/faq/{} - Deleting FAQ", uuid);

        try {
            faqService.deleteFAQ(uuid);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{uuid}/publish")
    @Operation(summary = "Publish FAQ", description = "Publishes an FAQ")
    public ResponseEntity<FAQ> publishFAQ(@Parameter(description = "FAQ UUID") @PathVariable UUID uuid) {

        log.debug("PATCH /api/v1/admin/faq/{}/publish - Publishing FAQ", uuid);

        try {
            FAQ faq = faqService.publishFAQ(uuid);
            return ResponseEntity.ok(faq);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{uuid}/unpublish")
    @Operation(summary = "Unpublish FAQ", description = "Unpublishes an FAQ")
    public ResponseEntity<FAQ> unpublishFAQ(@Parameter(description = "FAQ UUID") @PathVariable UUID uuid) {
        log.debug("PATCH /api/v1/admin/faq/{}/unpublish - Unpublishing FAQ", uuid);

        try {
            FAQ faq = faqService.unpublishFAQ(uuid);
            return ResponseEntity.ok(faq);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{uuid}/feature")
    @Operation(summary = "Feature FAQ", description = "Marks an FAQ as featured")
    public ResponseEntity<FAQ> featureFAQ(
            @Parameter(description = "FAQ UUID") @PathVariable UUID uuid,
            @Parameter(description = "Admin user UUID") @RequestHeader("X-User-UUID") UUID adminUuid) {

        log.debug("PATCH /api/v1/admin/faq/{}/feature - Featuring FAQ", uuid);

        try {
            FAQ faq = faqService.featureFAQ(uuid, adminUuid);
            return ResponseEntity.ok(faq);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{uuid}/unfeature")
    @Operation(summary = "Unfeature FAQ", description = "Removes featured status from an FAQ")
    public ResponseEntity<FAQ> unfeatureFAQ(
            @Parameter(description = "FAQ UUID") @PathVariable UUID uuid,
            @Parameter(description = "Admin user UUID") @RequestHeader("X-User-UUID") UUID adminUuid) {

        log.debug("PATCH /api/v1/admin/faq/{}/unfeature - Unfeaturing FAQ", uuid);

        try {
            FAQ faq = faqService.unfeatureFAQ(uuid, adminUuid);
            return ResponseEntity.ok(faq);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/reorder")
    @Operation(summary = "Reorder FAQs", description = "Updates the display order of FAQs")
    public ResponseEntity<Void> reorderFAQs(
            @Parameter(description = "Ordered list of FAQ UUIDs") @RequestBody List<UUID> orderedUuids) {

        log.debug("PUT /api/v1/admin/faq/reorder - Reordering {} FAQs", orderedUuids.size());

        faqService.reorderFAQs(orderedUuids);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/category/{oldCategoryUuid}/move/{newCategoryUuid}")
    @Operation(summary = "Move FAQs between categories", description = "Moves all FAQs from one category to another")
    public ResponseEntity<Void> moveFAQsBetweenCategories(
            @Parameter(description = "Source category UUID") @PathVariable UUID oldCategoryUuid,
            @Parameter(description = "Target category UUID") @PathVariable UUID newCategoryUuid,
            @Parameter(description = "Admin user UUID") @RequestHeader("X-User-UUID") UUID adminUuid) {

        log.debug("PUT /api/v1/admin/faq/category/{}/move/{} - Moving FAQs between categories", oldCategoryUuid, newCategoryUuid);

        try {
            faqService.moveFAQsBetweenCategories(oldCategoryUuid, newCategoryUuid, adminUuid);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/analytics/stats")
    @Operation(summary = "Get FAQ statistics", description = "Retrieves FAQ statistics for dashboard")
    public ResponseEntity<FAQStats> getFAQStats() {
        log.debug("GET /api/v1/admin/faq/analytics/stats - Fetching FAQ statistics");

        FAQStats stats = faqService.getFAQStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/validate/slug")
    @Operation(summary = "Validate FAQ slug", description = "Checks if an FAQ slug is unique within a category")
    public ResponseEntity<Boolean> validateFAQSlug(
            @Parameter(description = "FAQ slug to validate") @RequestParam String slug,
            @Parameter(description = "Category UUID") @RequestParam UUID categoryUuid,
            @Parameter(description = "FAQ UUID to exclude from validation") @RequestParam(required = false) UUID excludeUuid) {

        log.debug("GET /api/v1/admin/faq/validate/slug - Validating slug: {} in category: {}", slug, categoryUuid);

        boolean isUnique = faqService.isFAQSlugUnique(slug, categoryUuid, excludeUuid);
        return ResponseEntity.ok(isUnique);
    }

}
