package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.FAQ.FAQResponseDTO;
import com.mentors.applicationstarter.DTO.FAQ.FAQVoteRequest;
import com.mentors.applicationstarter.DTO.FAQCategory.FAQCategoryPublicResponseDTO;
import com.mentors.applicationstarter.DTO.FAQRequest;
import com.mentors.applicationstarter.DTO.FAQStats;
import com.mentors.applicationstarter.Enum.FAQPriority;
import com.mentors.applicationstarter.Enum.FAQStatus;
import com.mentors.applicationstarter.Model.FAQ;
import com.mentors.applicationstarter.Model.FAQCategory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * FAQ Service Interface
 * Provides comprehensive FAQ management functionality for both public and admin use
 */

@Service
public interface FAQService {
    // ================================
    // PUBLIC API METHODS (for frontend users)
    // ================================

    List<FAQCategoryPublicResponseDTO> getAllPublishedFAQs();
    List<FAQResponseDTO> getFAQsByCategoryUuid(UUID categoryUuid);
    FAQResponseDTO getFAQById(String identifier);


    // ================================
    // ADMIN API METHODS (for backend management)
    // ================================

    List<FAQResponseDTO> getAll();
    FAQResponseDTO createFAQ(FAQRequest faq);
    FAQ updateFAQ(UUID uuid, FAQRequest faq);
    FAQResponseDTO publishFAQ(UUID uuid);
    FAQResponseDTO unpublishFAQ(UUID uuid);
    FAQResponseDTO featureFAQ(UUID uuid);
    FAQResponseDTO unfeatureFAQ(UUID uuid);
    FAQResponseDTO linkFaqToCategory(UUID faqUuid, UUID categoryUuid);

    void deleteFAQ(UUID uuid);
    void moveFAQsBetweenCategories(UUID oldCategoryUuid, UUID newCategoryUuid, UUID updatedBy);
    FAQResponseDTO unlinkFAQ(UUID uuid);

    void recordFAQView(UUID faqUuid, HttpServletRequest request);

    FAQResponseDTO voteFAQ(UUID faqUuid, FAQVoteRequest requestBody, HttpServletRequest request);

    // ================================
    // VALIDATION METHODS
    // ================================

    // ================================
    // ANALYTICS AND STATISTICS
    // ================================

}
