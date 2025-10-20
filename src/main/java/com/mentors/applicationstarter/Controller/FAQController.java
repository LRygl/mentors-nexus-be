package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.DTO.FAQ.FAQResponseDTO;
import com.mentors.applicationstarter.Model.FAQ;
import com.mentors.applicationstarter.Service.FAQService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/faq")
@RequiredArgsConstructor
@Tag(name = "FAQ Public API", description = "Public FAQ operations for end users")
public class FAQController {

    private final FAQService faqService;

    @GetMapping
    @Operation(summary = "Get all published FAQs", description = "Retrieves all published FAQs ordered by priority and display order")
    public ResponseEntity<List<FAQResponseDTO>> getAllPublishedFAQs() {
        log.debug("GET /api/v1/faq - Fetching all published FAQs");
        List<FAQResponseDTO> faqs = faqService.getAllPublishedFAQs();
        return ResponseEntity.ok(faqs);
    }


    @GetMapping("/category/{categoryUuid}")
    @Operation(summary = "Get FAQs by category UUID", description = "Retrieves published FAQs for a specific category using UUID")
    public ResponseEntity<List<FAQResponseDTO>> getFAQsByCategoryUuid(
            @Parameter(description = "Category UUID") @PathVariable UUID categoryUuid) {
        log.debug("GET /api/v1/faq/category/uuid/{} - Fetching FAQs by category UUID", categoryUuid);
        List<FAQResponseDTO> faqs = faqService.getFAQsByCategoryUuid(categoryUuid);
        return ResponseEntity.ok(faqs);
    }


}
