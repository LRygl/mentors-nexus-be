package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.CategoryFAQCount;
import com.mentors.applicationstarter.DTO.FAQStats;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Enum.FAQPriority;
import com.mentors.applicationstarter.Enum.FAQStatus;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.FAQ;
import com.mentors.applicationstarter.Model.FAQCategory;
import com.mentors.applicationstarter.Repository.FAQCategoryRepository;
import com.mentors.applicationstarter.Repository.FAQRepository;
import com.mentors.applicationstarter.Service.FAQCategoryService;
import com.mentors.applicationstarter.Service.FAQService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FAQServiceImpl implements FAQService {

    private final FAQRepository faqRepository;
    private final FAQCategoryRepository faqCategoryRepository;
    private final FAQCategoryService faqCategoryService;

    // ================================
    // PUBLIC API METHODS
    // ================================

    @Override
    @Transactional(readOnly = true)
    public List<FAQ> getAllPublishedFAQs() {
        log.debug("Fetching all published FAQs");
        return faqRepository.findAllPublishedFAQs();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FAQ> getFAQsByCategory(FAQCategory category) {
        log.debug("Fetching FAQs for category: {}", category.getName());
        return faqRepository.findPublishedFAQsByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FAQ> getFAQsByCategorySlug(String categorySlug) {
        log.debug("Fetching FAQs for category slug: {}", categorySlug);
        return faqRepository.findPublishedFAQsByCategorySlug(categorySlug);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FAQ> getFAQsByCategoryUuid(UUID categoryUuid) {
        log.debug("Fetching FAQs for category UUID: {}", categoryUuid);
        return faqRepository.findPublishedFAQsByCategoryUuid(categoryUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FAQ> getFeaturedFAQs() {
        log.debug("Fetching featured FAQs");
        return faqRepository.findFeaturedFAQs();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FAQ> searchFAQs(String searchTerm) {
        log.debug("Searching FAQs with term: {}", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllPublishedFAQs();
        }

        // Use relevance ranking for better search results
        return faqRepository.searchWithRelevanceRanking(searchTerm.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FAQ> searchFAQsInCategory(String searchTerm, String categorySlug) {
        log.debug("Searching FAQs with term: {} in category: {}", searchTerm, categorySlug);

        Optional<FAQCategory> category = faqCategoryService.getCategoryBySlug(categorySlug);
        if (category.isEmpty()) {
            log.warn("Category not found with slug: {}", categorySlug);
            return List.of();
        }

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getFAQsByCategory(category.get());
        }

        return faqRepository.searchPublishedFAQsInCategory(searchTerm.trim(), category.get());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FAQ> getFAQByUuid(UUID uuid) {
        log.debug("Fetching FAQ by UUID: {}", uuid);
        Optional<FAQ> faq = faqRepository.findByUuid(uuid);

        // Only return if published for public access
        return faq.filter(f -> f.getIsPublished() && f.getStatus() == FAQStatus.PUBLISHED);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FAQ> getFAQBySlug(String slug) {
        log.debug("Fetching FAQ by slug: {}", slug);
        Optional<FAQ> faq = faqRepository.findBySlug(slug);

        // Only return if published for public access
        return faq.filter(f -> f.getIsPublished() && f.getStatus() == FAQStatus.PUBLISHED);
    }

    @Override
    @Async
    public void recordFAQView(UUID uuid) {
        log.debug("Recording view for FAQ: {}", uuid);
        try {
            faqRepository.incrementViewCount(uuid);
        } catch (Exception e) {
            log.warn("Failed to increment view count for FAQ: {}", uuid, e);
        }
    }

    @Override
    public void voteFAQHelpful(UUID uuid, boolean isHelpful) {
        log.debug("Recording {} vote for FAQ: {}", isHelpful ? "helpful" : "not helpful", uuid);
        try {
            if (isHelpful) {
                faqRepository.incrementHelpfulVotes(uuid);
            } else {
                faqRepository.incrementNotHelpfulVotes(uuid);
            }
        } catch (Exception e) {
            log.warn("Failed to record vote for FAQ: {}", uuid, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FAQ> getMostViewedFAQs(int limit) {
        log.debug("Fetching {} most viewed FAQs", limit);
        return faqRepository.findMostViewedFAQs(PageRequest.of(0, limit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FAQ> getMostViewedFAQsInCategory(String categorySlug, int limit) {
        log.debug("Fetching {} most viewed FAQs in category: {}", limit, categorySlug);

        Optional<FAQCategory> category = faqCategoryService.getCategoryBySlug(categorySlug);
        if (category.isEmpty()) {
            log.warn("Category not found with slug: {}", categorySlug);
            return List.of();
        }

        return faqRepository.findMostViewedFAQsInCategory(category.get(), PageRequest.of(0, limit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FAQ> getMostHelpfulFAQs(int limit) {
        log.debug("Fetching {} most helpful FAQs", limit);
        return faqRepository.findMostHelpfulFAQs(PageRequest.of(0, limit));
    }

    // ================================
    // ADMIN API METHODS
    // ================================

    @Override
    @Transactional(readOnly = true)
    public Page<FAQ> getAllFAQsForAdmin(Pageable pageable) {
        log.debug("Fetching all FAQs for admin with pagination");
        return faqRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FAQ> getFAQsByStatus(FAQStatus status, Pageable pageable) {
        log.debug("Fetching FAQs by status: {}", status);
        return faqRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FAQ> getFAQsByCategoryForAdmin(UUID categoryUuid, Pageable pageable) {
        log.debug("Fetching FAQs by category UUID for admin: {}", categoryUuid);
        return faqRepository.findByCategoryUuidOrderByDisplayOrderAsc(categoryUuid, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FAQ> getFAQsByFilters(FAQStatus status, UUID categoryUuid,
                                      FAQPriority priority, String search, Pageable pageable) {

        // Convert parameters to strings for native query
        String statusStr = status != null ? status.name() : null;
        String categoryUuidStr = categoryUuid != null ? categoryUuid.toString() : null;
        String priorityStr = priority != null ? priority.name() : null;
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        // Calculate offset
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        int limit = pageable.getPageSize();

        // Get results and count
        List<FAQ> content = faqRepository.findByFiltersNative(
                statusStr, categoryUuidStr, priorityStr, cleanSearch, limit, offset);

        long total = faqRepository.countByFiltersNative(
                statusStr, categoryUuidStr, priorityStr, cleanSearch);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public FAQ createFAQ(FAQ faq, UUID createdBy) {
        log.debug("Creating new FAQ: {}", faq.getQuestion());

        validateFAQ(faq);
        validateCategoryExists(faq.getCategory());

        // Generate unique slug within category
        String slug = generateUniqueSlug(faq.getQuestion(), faq.getCategory(), null);

        FAQ newFAQ = FAQ.builder()
                .uuid(UUID.randomUUID())
                .question(faq.getQuestion().trim())
                .answer(faq.getAnswer().trim())
                .category(faq.getCategory())
                .status(FAQStatus.DRAFT)
                .displayOrder(faq.getDisplayOrder() != null ? faq.getDisplayOrder() : 0)
                .isPublished(false)
                .isFeatured(faq.getIsFeatured() != null ? faq.getIsFeatured() : false)
                .searchKeywords(faq.getSearchKeywords())
                .metaDescription(faq.getMetaDescription())
                .slug(slug)
                .priority(faq.getPriority() != null ? faq.getPriority() : FAQPriority.NORMAL)
                .viewCount(0L)
                .helpfulVotes(0)
                .notHelpfulVotes(0)
                .createdBy(createdBy)
                .build();

        return faqRepository.save(newFAQ);
    }

    @Override
    public FAQ updateFAQ(UUID uuid, FAQ faq, UUID updatedBy) {
        log.debug("Updating FAQ: {}", uuid);

        FAQ existingFAQ = faqRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_NOT_FOUND));

        validateFAQ(faq);
        validateCategoryExists(faq.getCategory());

        // Generate new slug if question changed
        String slug = existingFAQ.getSlug();
        if (!existingFAQ.getQuestion().equals(faq.getQuestion())) {
            slug = generateUniqueSlug(faq.getQuestion(), faq.getCategory(), uuid);
        }

        existingFAQ.setQuestion(faq.getQuestion().trim());
        existingFAQ.setAnswer(faq.getAnswer().trim());
        existingFAQ.setCategory(faq.getCategory());
        existingFAQ.setDisplayOrder(faq.getDisplayOrder());
        existingFAQ.setSearchKeywords(faq.getSearchKeywords());
        existingFAQ.setMetaDescription(faq.getMetaDescription());
        existingFAQ.setSlug(slug);
        existingFAQ.setPriority(faq.getPriority() != null ? faq.getPriority() : FAQPriority.NORMAL);
        existingFAQ.setUpdatedBy(updatedBy);

        // Only update featured status if provided
        if (faq.getIsFeatured() != null) {
            existingFAQ.setIsFeatured(faq.getIsFeatured());
        }

        return faqRepository.save(existingFAQ);
    }

    @Override
    public void deleteFAQ(UUID uuid) {
        log.debug("Deleting FAQ: {}", uuid);
        FAQ faq = faqRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_NOT_FOUND));

        faqRepository.delete(faq);
    }

    @Override
    public FAQ publishFAQ(UUID faqUuid) {
        FAQ faq = faqRepository.findByUuid(faqUuid)
                .orElseThrow(() -> new RuntimeException("FAQ not found with UUID: " + faqUuid));

        // Update status
        faq.setStatus(FAQStatus.PUBLISHED);
        faq.setIsPublished(true);
        faq.setUpdatedAt(LocalDateTime.now());

        // Ensure unique slug before publishing
        ensureUniqueSlug(faq);

        return faqRepository.save(faq);
    }

    @Override
    public FAQ unpublishFAQ(UUID faqUuid) {
        FAQ faq = faqRepository.findByUuid(faqUuid)
                .orElseThrow(() -> new RuntimeException("FAQ not found with UUID: " + faqUuid));

        // Update status
        faq.setStatus(FAQStatus.DRAFT);
        faq.setIsPublished(false);
        faq.setUpdatedAt(LocalDateTime.now());

        return faqRepository.save(faq);
    }

    @Override
    public FAQ featureFAQ(UUID uuid, UUID updatedBy) {
        log.debug("Featuring FAQ: {}", uuid);
        FAQ faq = faqRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_NOT_FOUND));

        faq.setIsFeatured(true);
        faq.setUpdatedBy(updatedBy);

        return faqRepository.save(faq);
    }

    @Override
    public FAQ unfeatureFAQ(UUID uuid, UUID updatedBy) {
        log.debug("Unfeaturing FAQ: {}", uuid);
        FAQ faq = faqRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_NOT_FOUND));

        faq.setIsFeatured(false);
        faq.setUpdatedBy(updatedBy);

        return faqRepository.save(faq);
    }

    @Override
    public void reorderFAQs(List<UUID> orderedUuids) {
        log.debug("Reordering {} FAQs", orderedUuids.size());

        AtomicInteger order = new AtomicInteger(1);
        orderedUuids.forEach(uuid -> {
            FAQ faq = faqRepository.findByUuid(uuid).orElse(null);
            if (faq != null) {
                faq.setDisplayOrder(order.getAndIncrement());
                faqRepository.save(faq);
            }
        });
    }

    @Override
    public void moveFAQsBetweenCategories(UUID oldCategoryUuid, UUID newCategoryUuid, UUID updatedBy) {
        log.debug("Moving FAQs from category {} to category {}", oldCategoryUuid, newCategoryUuid);

        FAQCategory oldCategory = faqCategoryRepository.findByUuid(oldCategoryUuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_CATEGORY_NOT_FOUND));

        FAQCategory newCategory = faqCategoryRepository.findByUuid(newCategoryUuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_CATEGORY_NOT_FOUND));

        faqRepository.moveFAQsToNewCategory(oldCategory, newCategory, updatedBy);
    }

    // ================================
    // VALIDATION METHODS
    // ================================

    @Override
    @Transactional(readOnly = true)
    public boolean isFAQSlugUnique(String slug, UUID categoryUuid, UUID excludeUuid) {
        FAQCategory category = faqCategoryRepository.findByUuid(categoryUuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_CATEGORY_NOT_FOUND));

        return !faqRepository.existsBySlugAndCategoryAndUuidNot(slug, category, excludeUuid);
    }

    // ================================
    // ANALYTICS AND STATISTICS
    // ================================

    @Override
    @Transactional(readOnly = true)
    public FAQStats getFAQStats() {
        log.debug("Fetching FAQ statistics");

        Long totalFAQs = faqRepository.count();
        Long publishedFAQs = getPublishedFAQCount();
        Long draftFAQs = getFAQCountByStatus(FAQStatus.DRAFT);
        Long featuredFAQs = getFeaturedFAQCount();

        // Calculate total views and votes
        List<FAQ> allFAQs = faqRepository.findAll();
        Long totalViews = allFAQs.stream().mapToLong(FAQ::getViewCount).sum();
        Long totalHelpfulVotes = allFAQs.stream().mapToLong(FAQ::getHelpfulVotes).sum();

        List<FAQ> mostViewedFAQs = getMostViewedFAQs(5);
        List<FAQ> mostHelpfulFAQs = getMostHelpfulFAQs(5);

        // Get FAQ counts by category
        List<CategoryFAQCount> faqsByCategory = faqCategoryService.getCategoriesWithFAQCounts()
                .stream()
                .map(category -> CategoryFAQCount.builder()
                        .categoryName(category.getName())
                        .categorySlug(category.getSlug())
                        .categoryUuid(category.getUuid().toString())
                        .totalFAQs(category.getFaqCount())
                        .publishedFAQs(category.getPublishedFaqCount())
                        .build())
                .collect(Collectors.toList());

        return FAQStats.builder()
                .totalFAQs(totalFAQs)
                .publishedFAQs(publishedFAQs)
                .draftFAQs(draftFAQs)
                .featuredFAQs(featuredFAQs)
                .totalViews(totalViews)
                .totalHelpfulVotes(totalHelpfulVotes)
                .mostViewedFAQs(mostViewedFAQs)
                .mostHelpfulFAQs(mostHelpfulFAQs)
                .faqsByCategory(faqsByCategory)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getFAQCountByStatus(FAQStatus status) {
        return faqRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getFAQCountByPriority(FAQPriority priority) {
        return faqRepository.countByPriority(priority);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPublishedFAQCount() {
        return faqRepository.countPublishedFAQs();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getFeaturedFAQCount() {
        return faqRepository.countFeaturedFAQs();
    }

    // ================================
    // PRIVATE HELPER METHODS
    // ================================

    private void validateFAQ(FAQ faq) {
        if (faq.getQuestion() == null || faq.getQuestion().trim().isEmpty()) {
            throw new IllegalArgumentException("FAQ question cannot be empty");
        }

        if (faq.getAnswer() == null || faq.getAnswer().trim().isEmpty()) {
            throw new IllegalArgumentException("FAQ answer cannot be empty");
        }

        if (faq.getCategory() == null) {
            throw new IllegalArgumentException("FAQ category is required");
        }

        if (faq.getQuestion().length() > 500) {
            throw new IllegalArgumentException("FAQ question too long (max 500 characters)");
        }
    }

    private void validateCategoryExists(FAQCategory category) {
        if (category.getUuid() == null) {
            throw new IllegalArgumentException("Category UUID is required");
        }

        Optional<FAQCategory> existingCategory = faqCategoryService.getCategoryByUuid(category.getUuid());
        if (existingCategory.isEmpty()) {
            throw new ResourceNotFoundException(ErrorCodes.FAQ_CATEGORY_NOT_FOUND);
        }

        if (!existingCategory.get().getIsActive()) {
            throw new IllegalArgumentException("Cannot assign FAQ to inactive category");
        }
    }

    private void validateFAQForPublication(FAQ faq) {
        validateFAQ(faq);

        if (!faq.getCategory().getIsActive()) {
            throw new IllegalArgumentException("Cannot publish FAQ in inactive category");
        }

        if (faq.getStatus() == FAQStatus.ARCHIVED) {
            throw new IllegalArgumentException("Cannot publish archived FAQ");
        }
    }

    private String generateUniqueSlug(String question, FAQCategory category, UUID excludeUuid) {
        String baseSlug = generateSlug(question);
        String slug = baseSlug;
        int counter = 1;

        while (faqRepository.existsBySlugAndCategoryAndUuidNot(slug, category, excludeUuid)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    /**
     * FIX: Ensures the FAQ has a unique slug
     * This prevents the StringIndexOutOfBoundsException and ensures uniqueness
     */
    private void ensureUniqueSlug(FAQ faq) {
        if (faq.getQuestion() == null || faq.getQuestion().trim().isEmpty()) {
            faq.setSlug("faq-" + System.currentTimeMillis());
            return;
        }

        String baseSlug = generateSafeSlug(faq.getQuestion());
        String finalSlug = baseSlug;
        int attempt = 0;

        // Check for uniqueness and add suffix if needed
        while (slugExists(finalSlug, faq.getCategory().getId(), faq.getUuid())) {
            attempt++;
            finalSlug = baseSlug + "-" + attempt;

            // Prevent infinite loop
            if (attempt > 100) {
                finalSlug = baseSlug + "-" + System.currentTimeMillis();
                break;
            }
        }

        faq.setSlug(finalSlug);
    }

    /**
     * FIX: Safe slug generation with proper bounds checking
     */
    private String generateSafeSlug(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "faq-" + System.currentTimeMillis();
        }

        // Clean and normalize
        String normalized = text.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        // Safe truncation
        final int MAX_LENGTH = 50;
        if (normalized.length() <= MAX_LENGTH) {
            return normalized.isEmpty() ? "faq" : normalized;
        }

        // Truncate at word boundary
        String truncated = normalized.substring(0, MAX_LENGTH);
        int lastHyphen = truncated.lastIndexOf('-');

        if (lastHyphen > 0 && lastHyphen > MAX_LENGTH - 10) {
            return normalized.substring(0, lastHyphen);
        }

        return truncated;
    }

    /**
     * Check if slug already exists for this category (excluding current FAQ)
     */
    private boolean slugExists(String slug, Long categoryId, UUID excludeUuid) {
        return faqRepository.existsBySlugAndCategoryIdAndUuidNot(slug, categoryId, excludeUuid);
    }


    private String generateSlug(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String slug = text.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "") // Remove special characters
                .replaceAll("\\s+", "-") // Replace spaces with hyphens
                .replaceAll("-+", "-") // Replace multiple hyphens with single
                .replaceAll("^-|-$", ""); // Remove leading/trailing hyphens

        return slug.substring(0, Math.min(100, slug.length())); // Use slug.length()
    }
}
