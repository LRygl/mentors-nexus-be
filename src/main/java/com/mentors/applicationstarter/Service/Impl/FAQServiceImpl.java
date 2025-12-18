package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.FAQ.FAQResponseDTO;
import com.mentors.applicationstarter.DTO.FAQ.FAQVoteRequest;
import com.mentors.applicationstarter.DTO.FAQCategory.FAQCategoryPublicResponseDTO;
import com.mentors.applicationstarter.DTO.FAQRequest;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Enum.FAQInteractionType;
import com.mentors.applicationstarter.Enum.FAQPriority;
import com.mentors.applicationstarter.Enum.FAQStatus;
import com.mentors.applicationstarter.Exception.InvalidRequestException;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Mapper.FAQCategoryMapper;
import com.mentors.applicationstarter.Mapper.FAQMapper;
import com.mentors.applicationstarter.Model.FAQ;
import com.mentors.applicationstarter.Model.FAQAnalytics;
import com.mentors.applicationstarter.Model.FAQCategory;
import com.mentors.applicationstarter.Repository.FAQAnalyticsRepository;
import com.mentors.applicationstarter.Repository.FAQCategoryRepository;
import com.mentors.applicationstarter.Repository.FAQRepository;
import com.mentors.applicationstarter.Service.FAQCategoryService;
import com.mentors.applicationstarter.Service.FAQService;
import com.mentors.applicationstarter.Utils.EntityLookupUtils;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FAQServiceImpl implements FAQService {

    private final FAQRepository faqRepository;
    private final FAQCategoryRepository faqCategoryRepository;
    private final FAQCategoryService faqCategoryService;
    private final FAQAnalyticsRepository faqAnalyticsRepository;

    // ================================
    // PUBLIC API METHODS
    // ================================

    @Override
    @Transactional(readOnly = true)
    public List<FAQCategoryPublicResponseDTO> getAllPublishedFAQs() {

        List<FAQ> publishedFaqs = faqRepository.findAllPublishedWithCategory();

        return publishedFaqs.stream()
                .filter(faq -> faq.getCategory() != null)
                .collect(Collectors.groupingBy(FAQ::getCategory))
                .entrySet()
                .stream()
                .map(entry -> FAQCategoryMapper.toPublicResponseDTO(
                        entry.getKey(),
                        entry.getValue()
                ))
                .sorted(Comparator.comparing(FAQCategoryPublicResponseDTO::getDisplayOrder))
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public List<FAQResponseDTO> getFAQsByCategoryUuid(UUID categoryUuid) {
        //TODO Add checks to verify that the category exists by uuid oterwise throw exception
        return faqRepository.findPublishedFAQsByCategoryUuid(categoryUuid).stream()
                .map(FAQMapper::toFaqResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public FAQResponseDTO getFAQById(String identifier) {
        FAQ faq = EntityLookupUtils.findByIdentifier(
                identifier,
                faqRepository,
                ErrorCodes.FAQ_NOT_FOUND,
                ErrorCodes.FAQ_CATEGORY_REQUIRED
        );

        return FAQMapper.toFaqResponseDto(faq);

    }

    // ================================
    // ADMIN API METHODS
    // ================================

    @Override
    public List<FAQResponseDTO> getAll() {
        return faqRepository.findAll().stream()
                .map(FAQMapper::toFaqResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public FAQResponseDTO createFAQ(FAQRequest faq) {
        log.debug("Creating new FAQ: {}", faq.getQuestion());

        validateFAQ(faq);
        FAQCategory category = null;
        if (faq.getCategoryId() != null) {
            try {
                category = faqCategoryService.getFAQCategoryEntityById(faq.getCategoryId());
            } catch (Exception e){
                log.warn("Category with ID {} not found, creating FAQ without category", faq.getCategoryId());
            }
        }


        // Generate unique slug within category
        String slug = generateUniqueSlug(faq.getQuestion(), category, null);

        FAQ newFAQ = FAQ.builder()
                .uuid(UUID.randomUUID())
                .question(faq.getQuestion().trim())
                .answer(faq.getAnswer().trim())
                .category(category)
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
                .build();

        FAQ savedFaq = faqRepository.save(newFAQ);
        return FAQMapper.toFaqResponseDto(savedFaq);
    }

    @Override
    public FAQ updateFAQ(UUID uuid, FAQRequest faq) {
        log.debug("Updating FAQ: {}", uuid);

        FAQ existingFAQ = faqRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_NOT_FOUND));

        validateFAQ(faq);
        FAQCategory category = faqCategoryService.getFAQCategoryEntityById(faq.getCategoryId());

        // Generate new slug if question changed
        String slug = existingFAQ.getSlug();
        if (!existingFAQ.getQuestion().equals(faq.getQuestion())) {
            slug = generateUniqueSlug(faq.getQuestion(), category, uuid);
        }

        existingFAQ.setQuestion(faq.getQuestion().trim());
        existingFAQ.setAnswer(faq.getAnswer().trim());
        if(faq.getCategoryId() != null) {
            existingFAQ.setCategory(category);
        }

        if(faq.getIsPublished() != null) {
            existingFAQ.setIsPublished(faq.getIsPublished());
        }

        if(faq.getIsFeatured() != null) {
            existingFAQ.setIsFeatured(faq.getIsFeatured());
        }

        existingFAQ.setDisplayOrder(faq.getDisplayOrder());
        existingFAQ.setSearchKeywords(faq.getSearchKeywords());
        existingFAQ.setMetaDescription(faq.getMetaDescription());
        existingFAQ.setSlug(slug);
        existingFAQ.setPriority(faq.getPriority() != null ? faq.getPriority() : FAQPriority.NORMAL);

        // Only update featured status if provided
        if (faq.getIsFeatured() != null) {
            existingFAQ.setIsFeatured(faq.getIsFeatured());
        }

        return faqRepository.save(existingFAQ);
    }

    @Override
    public void deleteFAQ(UUID uuid) {
        FAQ faq = faqRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_NOT_FOUND));

        faqRepository.delete(faq);
    }

    //TODO Add missing category handling
    @Override
    public FAQResponseDTO publishFAQ(UUID faqUuid) {
        FAQ faq = faqRepository.findByUuid(faqUuid)
                .orElseThrow(() -> new RuntimeException("FAQ not found with UUID: " + faqUuid));

        if (faq.getCategory() == null) {
            log.error("FAQ category is null, throwing exception");
            throw new InvalidRequestException(ErrorCodes.FAQ_CATEGORY_REQUIRED);
        }
        // Update status
        faq.setStatus(FAQStatus.PUBLISHED);
        faq.setIsPublished(true);
        faq.setUpdatedAt(LocalDateTime.now());

        // Ensure unique slug before publishing
        ensureUniqueSlug(faq);
        FAQ savedFaq = faqRepository.save(faq);
        return FAQMapper.toFaqResponseDto(savedFaq);
    }

    @Override
    public FAQResponseDTO unpublishFAQ(UUID faqUuid) {
        FAQ faq = faqRepository.findByUuid(faqUuid)
                .orElseThrow(() -> new RuntimeException("FAQ not found with UUID: " + faqUuid));

        // Update status
        faq.setStatus(FAQStatus.DRAFT);
        faq.setIsPublished(false);
        faq.setUpdatedAt(LocalDateTime.now());
        faq.setIsFeatured(false);

        FAQ savedFaq = faqRepository.save(faq);
        return FAQMapper.toFaqResponseDto(savedFaq);
    }

    @Override
    public FAQResponseDTO featureFAQ(UUID uuid) {
        log.debug("Featuring FAQ: {}", uuid);
        FAQ faq = faqRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_NOT_FOUND));

        faq.setIsFeatured(true);
        faq.setUpdatedAt(LocalDateTime.now());
        //faq.setUpdatedBy(updatedBy);

        FAQ savedFaq = faqRepository.save(faq);
        return FAQMapper.toFaqResponseDto(savedFaq);
    }

    @Override
    public FAQResponseDTO unfeatureFAQ(UUID uuid) {
        log.debug("Unfeaturing FAQ: {}", uuid);
        FAQ faq = faqRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_NOT_FOUND));

        faq.setIsFeatured(false);
        faq.setUpdatedAt(LocalDateTime.now());
        //faq.setUpdatedBy(updatedBy);

        FAQ savedFaq = faqRepository.save(faq);
        return FAQMapper.toFaqResponseDto(savedFaq);
    }

    @Override
    public void moveFAQsBetweenCategories(UUID oldCategoryUuid, UUID newCategoryUuid, UUID updatedBy) {
        log.debug("Moving FAQs from category {} to category {}", oldCategoryUuid, newCategoryUuid);

        FAQCategory oldCategory = faqCategoryRepository.findByUuid(oldCategoryUuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_CATEGORY_NOT_FOUND));

        FAQCategory newCategory = faqCategoryRepository.findByUuid(newCategoryUuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_CATEGORY_NOT_FOUND));

        faqRepository.moveFAQsToNewCategory(oldCategory, newCategory, updatedBy);

        recalcualteFAQDisplayOrder(oldCategory.getUuid(),null);
        recalcualteFAQDisplayOrder(newCategory.getUuid(),null);

    }

    @Override
    public FAQResponseDTO unlinkFAQ(UUID uuid) {
        FAQ faq = faqRepository.findByUuid(uuid).orElseThrow(
                ()-> new ResourceNotFoundException(ErrorCodes.FAQ_NOT_FOUND)
        );
        FAQCategory category = faq.getCategory();
        faq.setCategory(null);
        faq.setDisplayOrder(0);
        faq.setIsPublished(false);
        faq.setIsFeatured(false);
        FAQ savedFaq = faqRepository.save(faq);

        recalcualteFAQDisplayOrder(category.getUuid(),null);

        return FAQMapper.toFaqResponseDto(savedFaq);
    }



    @Override
    public FAQResponseDTO linkFaqToCategory(UUID faqUuid, UUID categoryUuid) {
        FAQ faq = faqRepository.findByUuid(faqUuid).orElseThrow(()->new ResourceNotFoundException(ErrorCodes.FAQ_NOT_FOUND));
        FAQCategory category = faqCategoryService.getCategoryByUuid(categoryUuid);

        //Get maximum displayOrder for category
        Integer maxDisplayOrder = faqRepository.findMaxDisplayOrderByCategory(category.getId())
                .orElse(0);

        faq.setCategory(category);
        faq.setDisplayOrder(maxDisplayOrder + 1);
        FAQ savedFaq = faqRepository.save(faq);
        return FAQMapper.toFaqResponseDto(savedFaq);
    }

    // ================================
    // VALIDATION METHODS
    // ================================

    // ================================
    // ANALYTICS AND STATISTICS
    // ================================

    @Override
    @Transactional
    public void recordFAQView(UUID faqUuid, HttpServletRequest request) {
        FAQ faq = faqRepository.findByUuid(faqUuid)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_NOT_FOUND));

        // Record detailed analytics
        String sessionId = request.getSession().getId();
        String ipAddress = request.getRemoteAddr();

        Instant viewWindow = Instant.now().minus(1, ChronoUnit.HOURS);

        Optional<FAQAnalytics> recentView = faqAnalyticsRepository.findRecentViewBySessionAndIpAndFaq(
                sessionId, ipAddress, faqUuid, viewWindow
        );

        if (recentView.isPresent()) {
            log.info("Recent view has been recorded: {}", recentView.get());
            return;
        }

        // Update aggregated view count
        faq.setViewCount(faq.getViewCount() + 1);
        faqRepository.save(faq);

        FAQAnalytics analytics = FAQAnalytics.builder()
                .uuid(UUID.randomUUID())
                .faqUuid(faqUuid)
                .interactionType(FAQInteractionType.VIEW)
                .sessionId(sessionId)
                .ipAddress(ipAddress)
                .build();

        faqAnalyticsRepository.save(analytics);

        log.info("Recorded view for FAQ: {} from session: {}, IP: {}", faqUuid, sessionId, ipAddress);
    }

    @Override
    @Transactional
    public FAQResponseDTO voteFAQ(UUID faqUuid, FAQVoteRequest requestBody, HttpServletRequest request) {

        FAQ faq = faqRepository.findByUuid(faqUuid).orElseThrow(
                () -> new ResourceNotFoundException(ErrorCodes.FAQ_NOT_FOUND));

        String sessionId = request.getSession().getId();
        String ipAddress = request.getRemoteAddr();

        // 3. Determine new vote type
        FAQInteractionType newVoteType = requestBody.isHelpful()
                ? FAQInteractionType.VOTE_HELPFUL
                : FAQInteractionType.VOTE_NOT_HELPFUL;

        // 4. Check for existing vote
        List<FAQInteractionType> voteTypes = List.of(
                FAQInteractionType.VOTE_HELPFUL,
                FAQInteractionType.VOTE_NOT_HELPFUL
        );

        Optional<FAQAnalytics> existingVote = faqAnalyticsRepository
                .findFirstByFaqUuidAndSessionIdAndIpAddressAndInteractionTypeInOrderByTimestampDesc(
                        faqUuid, sessionId, ipAddress, voteTypes
                );

        if (existingVote.isPresent()) {
            FAQAnalytics previousVote = existingVote.get();

            if (previousVote.getInteractionType() == newVoteType) {
                // Same vote - idempotent, return without changes
                log.info("Vote not counter - was counter in previous attempt: {}", previousVote);
                return FAQMapper.toFaqResponseDto(faq);
            }

            // User is changing their vote
            // 1. Decrement old vote counter
            updateVoteCounter(faq, previousVote.getInteractionType(), false);

            // 2. Increment new vote counter
            updateVoteCounter(faq, newVoteType, true);

            // 3. Record the vote change with history
            FAQAnalytics voteChange = FAQAnalytics.builder()
                    .uuid(UUID.randomUUID())
                    .faqUuid(faqUuid)
                    .interactionType(newVoteType)
                    .previousInteractionType(previousVote.getInteractionType()) // Track what changed
                    .sessionId(sessionId)
                    .ipAddress(ipAddress)
                    .build();

            faqAnalyticsRepository.save(voteChange);

        } else {
            // New vote
            updateVoteCounter(faq, newVoteType, true);

            FAQAnalytics newVote = FAQAnalytics.builder()
                    .uuid(UUID.randomUUID())
                    .faqUuid(faqUuid)
                    .interactionType(newVoteType)
                    .previousInteractionType(null) // First vote
                    .sessionId(sessionId)
                    .ipAddress(ipAddress)
                    .build();

            faqAnalyticsRepository.save(newVote);
        }

        FAQ result = faqRepository.save(faq);

        return FAQMapper.toFaqResponseDto(result);
    }


    // ================================
    // PRIVATE HELPER METHODS
    // ================================

    /**
     * Update vote counters on FAQ entity
     * Like Spring Boot's @Transactional - ensures atomic updates
     */
    private void updateVoteCounter(FAQ faq, FAQInteractionType voteType, boolean increment) {
        int delta = increment ? 1 : -1;

        if (voteType == FAQInteractionType.VOTE_HELPFUL) {
            faq.setHelpfulVotes(Math.max(0, faq.getHelpfulVotes() + delta));
        } else if (voteType == FAQInteractionType.VOTE_NOT_HELPFUL) {
            faq.setNotHelpfulVotes(Math.max(0, faq.getNotHelpfulVotes() + delta));
        }
    }

    /**
     * Method accepts FAQCategory UUID and Optional list of FAQUUID in order
     * If list is not provided, just renumber items sequentially, otherwise renumber based on the list
     */
    private void recalcualteFAQDisplayOrder(UUID categoryUUID, @Nullable List<UUID> faqUUIDOrder) {
        FAQCategory category = faqCategoryService.getCategoryByUuid(categoryUUID);

        //Return list of FAQs for category in ascending order
        List<FAQ> faqs = faqRepository.findByCategoryOrderByDisplayOrderAsc(category);

        if(faqUUIDOrder != null) {
            if (faqs.size() != faqUUIDOrder.size() ||
                    !faqs.stream().map(FAQ::getUuid).collect(Collectors.toSet())
                            .equals(new HashSet<>(faqUUIDOrder))) {
                throw new IllegalArgumentException("Invalid FAQ list for reordering");
            }
            // Apply the new order from the list
            for (int i = 0; i < faqUUIDOrder.size(); i++) {
                UUID faqUUID = faqUUIDOrder.get(i);
                FAQ faq = faqs.stream()
                        .filter(f -> f.getUuid().equals(faqUUID))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.FAQ_NOT_FOUND));
                faq.setDisplayOrder(i + 1);
            }
        } else {
            // No list was provided - renumber items sequentialy
            for (int i = 0; i < faqs.size(); i++) {
                faqs.get(i).setDisplayOrder(i + 1);
            }
        }

        faqRepository.saveAll(faqs);

    }

    private void validateFAQ(FAQRequest faq) {
        if (faq.getQuestion() == null || faq.getQuestion().trim().isEmpty()) {
            throw new IllegalArgumentException("FAQ question cannot be empty");
        }

        if (faq.getAnswer() == null || faq.getAnswer().trim().isEmpty()) {
            throw new IllegalArgumentException("FAQ answer cannot be empty");
        }

        if (faq.getQuestion().length() > 500) {
            throw new IllegalArgumentException("FAQ question too long (max 500 characters)");
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
