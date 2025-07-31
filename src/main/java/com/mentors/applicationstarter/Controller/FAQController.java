package com.mentors.applicationstarter.Controller;

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
import java.util.Optional;
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
    public ResponseEntity<List<FAQ>> getAllPublishedFAQs() {
        log.debug("GET /api/v1/faq - Fetching all published FAQs");
        List<FAQ> faqs = faqService.getAllPublishedFAQs();
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/category/{categorySlug}")
    @Operation(summary = "Get FAQs by category slug", description = "Retrieves published FAQs for a specific category using URL slug")
    public ResponseEntity<List<FAQ>> getFAQsByCategorySlug(
            @Parameter(description = "Category URL slug") @PathVariable String categorySlug) {
        log.debug("GET /api/v1/faq/category/{} - Fetching FAQs by category slug", categorySlug);
        List<FAQ> faqs = faqService.getFAQsByCategorySlug(categorySlug);
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/category/uuid/{categoryUuid}")
    @Operation(summary = "Get FAQs by category UUID", description = "Retrieves published FAQs for a specific category using UUID")
    public ResponseEntity<List<FAQ>> getFAQsByCategoryUuid(
            @Parameter(description = "Category UUID") @PathVariable UUID categoryUuid) {
        log.debug("GET /api/v1/faq/category/uuid/{} - Fetching FAQs by category UUID", categoryUuid);
        List<FAQ> faqs = faqService.getFAQsByCategoryUuid(categoryUuid);
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured FAQs", description = "Retrieves featured FAQs for homepage display")
    public ResponseEntity<List<FAQ>> getFeaturedFAQs() {
        log.debug("GET /api/v1/faq/featured - Fetching featured FAQs");
        List<FAQ> faqs = faqService.getFeaturedFAQs();
        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/search")
    @Operation(summary = "Search FAQs", description = "Search published FAQs by question, answer, keywords, or category")
    public ResponseEntity<List<FAQ>> searchFAQs(
            @Parameter(description = "Search term") @RequestParam(required = false) String q,
            @Parameter(description = "Category slug to search within") @RequestParam(required = false) String category) {
        log.debug("GET /api/v1/faq/search?q={}&category={} - Searching FAQs", q, category);

        List<FAQ> faqs;
        if (category != null && !category.trim().isEmpty()) {
            faqs = faqService.searchFAQsInCategory(q, category);
        } else {
            faqs = faqService.searchFAQs(q);
        }

        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Get FAQ by UUID", description = "Retrieves a specific FAQ and increments view count")
    public ResponseEntity<FAQ> getFAQByUuid(
            @Parameter(description = "FAQ UUID") @PathVariable UUID uuid) {
        log.debug("GET /api/v1/faq/{} - Fetching FAQ by UUID", uuid);
        Optional<FAQ> faq = faqService.getFAQByUuid(uuid);

        if (faq.isPresent()) {
            // Increment view count asynchronously
            faqService.recordFAQView(uuid);
            return ResponseEntity.ok(faq.get());
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/slug/{categorySlug}/{faqSlug}")
    @Operation(summary = "Get FAQ by category and FAQ slugs", description = "Retrieves a specific FAQ using SEO-friendly URLs")
    public ResponseEntity<FAQ> getFAQBySlug(
            @Parameter(description = "Category slug") @PathVariable String categorySlug,
            @Parameter(description = "FAQ slug") @PathVariable String faqSlug) {
        log.debug("GET /api/v1/faq/slug/{}/{} - Fetching FAQ by slugs", categorySlug, faqSlug);

        Optional<FAQ> faq = faqService.getFAQBySlug(faqSlug);

        if (faq.isPresent() && faq.get().getCategorySlug().equals(categorySlug)) {
            // Increment view count asynchronously
            faqService.recordFAQView(faq.get().getUuid());
            return ResponseEntity.ok(faq.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{uuid}/vote")
    @Operation(summary = "Vote on FAQ helpfulness", description = "Submit a helpful/not helpful vote for an FAQ")
    public ResponseEntity<Void> voteFAQHelpfulness(
            @Parameter(description = "FAQ UUID") @PathVariable UUID uuid,
            @Parameter(description = "Is the FAQ helpful?") @RequestParam boolean helpful) {
        log.debug("POST /api/v1/faq/{}/vote - Recording vote: {}", uuid, helpful);

        Optional<FAQ> faq = faqService.getFAQByUuid(uuid);
        if (faq.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        faqService.voteFAQHelpful(uuid, helpful);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/analytics/popular")
    @Operation(summary = "Get popular FAQs", description = "Retrieves most viewed FAQs for analytics")
    public ResponseEntity<List<FAQ>> getPopularFAQs(
            @Parameter(description = "Number of FAQs to return") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Category slug to filter by") @RequestParam(required = false) String category) {
        log.debug("GET /api/v1/faq/analytics/popular - Fetching {} popular FAQs in category: {}", limit, category);

        List<FAQ> faqs;
        if (category != null && !category.trim().isEmpty()) {
            faqs = faqService.getMostViewedFAQsInCategory(category, limit);
        } else {
            faqs = faqService.getMostViewedFAQs(limit);
        }

        return ResponseEntity.ok(faqs);
    }

    @GetMapping("/analytics/helpful")
    @Operation(summary = "Get most helpful FAQs", description = "Retrieves FAQs with highest helpful votes")
    public ResponseEntity<List<FAQ>> getMostHelpfulFAQs(
            @Parameter(description = "Number of FAQs to return") @RequestParam(defaultValue = "10") int limit) {
        log.debug("GET /api/v1/faq/analytics/helpful - Fetching {} most helpful FAQs", limit);
        List<FAQ> faqs = faqService.getMostHelpfulFAQs(limit);
        return ResponseEntity.ok(faqs);
    }
}
