package com.mentors.applicationstarter.Controller;


import com.mentors.applicationstarter.DTO.FAQ.FAQResponseDTO;
import com.mentors.applicationstarter.DTO.FAQRequest;
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
import org.springframework.data.repository.query.Param;
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
    public ResponseEntity<List<FAQResponseDTO>> getAllFaq() {
        return new ResponseEntity<>(faqService.getAll(),HttpStatus.OK);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<FAQResponseDTO> getFAQById(@PathVariable String identifier) {
        return new ResponseEntity<>(faqService.getFAQById(identifier), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create new FAQ", description = "Creates a new FAQ")
    public ResponseEntity<FAQResponseDTO> createFAQ(
            @Parameter(description = "FAQ data") @RequestBody FAQRequest faq) {

        try {
            FAQResponseDTO createdFAQ = faqService.createFAQ(faq);
            return ResponseEntity.ok(createdFAQ);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update FAQ", description = "Updates an existing FAQ")
    public ResponseEntity<FAQ> updateFAQ(
            @Parameter(description = "FAQ UUID") @PathVariable UUID uuid,
            @Parameter(description = "Updated FAQ data") @RequestBody FAQRequest faq,
            @Parameter(description = "Admin user UUID") @RequestHeader("X-User-UUID") UUID adminUuid) {

        try {
            FAQ updatedFAQ = faqService.updateFAQ(uuid, faq, adminUuid);
            return ResponseEntity.ok(updatedFAQ);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete FAQ", description = "Deletes an FAQ")
    public ResponseEntity<Void> deleteFAQ(
            @Parameter(description = "FAQ UUID") @PathVariable UUID uuid) {

        try {
            faqService.deleteFAQ(uuid);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{faqUuid}/link/{categoryUuid}")
    public ResponseEntity<FAQResponseDTO> linkFAQToCategory(@PathVariable UUID faqUuid, @PathVariable UUID categoryUuid) {
        FAQResponseDTO faq = faqService.linkFaqToCategory(faqUuid, categoryUuid);
        return ResponseEntity.ok(faq);
    }

    @PatchMapping("/{uuid}/unlink")
    public ResponseEntity<FAQResponseDTO> unlinkFaqFromCategory(@PathVariable UUID uuid){
        try {
            FAQResponseDTO faq = faqService.unlinkFAQ(uuid);
            return ResponseEntity.ok(faq);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{uuid}/publish")
    @Operation(summary = "Publish FAQ", description = "Publishes an FAQ")
    public ResponseEntity<FAQResponseDTO> publishFAQ(@Parameter(description = "FAQ UUID") @PathVariable UUID uuid) {
        try {
            FAQResponseDTO faq = faqService.publishFAQ(uuid);
            return ResponseEntity.ok(faq);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{uuid}/unpublish")
    @Operation(summary = "Unpublish FAQ", description = "Unpublishes an FAQ")
    public ResponseEntity<FAQResponseDTO> unpublishFAQ(@Parameter(description = "FAQ UUID") @PathVariable UUID uuid) {

        try {
            FAQResponseDTO faq = faqService.unpublishFAQ(uuid);
            return ResponseEntity.ok(faq);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
//@Parameter(description = "Admin user UUID") @RequestHeader("X-User-UUID") UUID adminUuid)
    @PatchMapping("/{uuid}/feature")
    @Operation(summary = "Feature FAQ", description = "Marks an FAQ as featured")
    public ResponseEntity<FAQResponseDTO> featureFAQ(
            @Parameter(description = "FAQ UUID") @PathVariable UUID uuid){

        try {
            FAQResponseDTO faq = faqService.featureFAQ(uuid);
            return ResponseEntity.ok(faq);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{uuid}/unfeature")
    @Operation(summary = "Unfeature FAQ", description = "Removes featured status from an FAQ")
    public ResponseEntity<FAQResponseDTO> unfeatureFAQ(
            @Parameter(description = "FAQ UUID") @PathVariable UUID uuid) {

        try {
            FAQResponseDTO faq = faqService.unfeatureFAQ(uuid);
            return ResponseEntity.ok(faq);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/category/{oldCategoryUuid}/move/{newCategoryUuid}")
    @Operation(summary = "Move FAQs between categories", description = "Moves all FAQs from one category to another")
    public ResponseEntity<Void> moveFAQsBetweenCategories(
            @Parameter(description = "Source category UUID") @PathVariable UUID oldCategoryUuid,
            @Parameter(description = "Target category UUID") @PathVariable UUID newCategoryUuid,
            @Parameter(description = "Admin user UUID") @RequestHeader("X-User-UUID") UUID adminUuid) {

        try {
            faqService.moveFAQsBetweenCategories(oldCategoryUuid, newCategoryUuid, adminUuid);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
