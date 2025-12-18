package com.mentors.applicationstarter.Controller.Public.FAQ;

import com.mentors.applicationstarter.DTO.FAQ.FAQResponseDTO;
import com.mentors.applicationstarter.DTO.FAQ.FAQVoteRequest;
import com.mentors.applicationstarter.DTO.FAQCategory.FAQCategoryPublicResponseDTO;
import com.mentors.applicationstarter.Mapper.FAQMapper;
import com.mentors.applicationstarter.Model.FAQ;
import com.mentors.applicationstarter.Service.FAQService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
public class FAQPublicController {

    private final FAQService faqService;

    @GetMapping
    @Operation(summary = "Get all published FAQs", description = "Retrieves all published FAQs ordered by priority and display order")
    public ResponseEntity<List<FAQCategoryPublicResponseDTO>> getAllPublishedFAQs() {
        log.debug("GET /api/v1/faq - Fetching all published FAQs");
        List<FAQCategoryPublicResponseDTO> faqs = faqService.getAllPublishedFAQs();
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


    @PostMapping("/{faqUuid}/view")
    @Operation(summary = "Record FAQ view", description = "Records when a user views/expands an FAQ")
    public ResponseEntity<Void> recordView(
            @PathVariable UUID faqUuid,
            HttpServletRequest request
    ) {
        log.debug("POST /api/v1/faq/{}/view - Recording FAQ view", faqUuid);
        faqService.recordFAQView(faqUuid, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{faqUuid}/vote")
    @Operation(
            summary = "Vote on FAQ helpfulness",
            description = "Vote helpful (true) or not helpful (false). Calling again with same value toggles off the vote."
    )
    public ResponseEntity<FAQResponseDTO> voteFAQ(
            @Parameter(description = "FAQ UUID")
            @PathVariable UUID faqUuid,
            @Parameter(description = "True for helpful, false for not helpful", example = "true")
            @RequestBody FAQVoteRequest requestBody,
            HttpServletRequest request
    ) {
        log.info("POST /api/v1/faq/{}/vote?helpful={} - Recording vote", faqUuid, requestBody.isHelpful());
        FAQResponseDTO updatedFaq = faqService.voteFAQ(faqUuid, requestBody, request);
        return ResponseEntity.ok(updatedFaq);
    }


}
