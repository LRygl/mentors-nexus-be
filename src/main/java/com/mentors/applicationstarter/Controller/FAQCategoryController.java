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



}