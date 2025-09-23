package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.FAQ.FAQResponseDTO;
import com.mentors.applicationstarter.DTO.FAQRequest;
import com.mentors.applicationstarter.DTO.FAQStats;
import com.mentors.applicationstarter.Enum.FAQPriority;
import com.mentors.applicationstarter.Enum.FAQStatus;
import com.mentors.applicationstarter.Model.FAQ;
import com.mentors.applicationstarter.Model.FAQCategory;
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

    List<FAQResponseDTO> getAllPublishedFAQs();
    List<FAQ> getFAQsByCategoryUuid(UUID categoryUuid);
    List<FAQ> getMostHelpfulFAQs(int limit);

    Optional<FAQ> getFAQByUuid(UUID uuid);

    // ================================
    // ADMIN API METHODS (for backend management)
    // ================================

    List<FAQResponseDTO> getAll();
    FAQ createFAQ(FAQRequest faq, UUID createdBy);
    FAQ updateFAQ(UUID uuid, FAQRequest faq, UUID updatedBy);
    FAQResponseDTO publishFAQ(UUID uuid);
    FAQResponseDTO unpublishFAQ(UUID uuid);
    FAQResponseDTO featureFAQ(UUID uuid, UUID updatedBy);
    FAQResponseDTO unfeatureFAQ(UUID uuid, UUID updatedBy);
    FAQResponseDTO linkFaqToCategory(UUID faqUuid, UUID categoryUuid);

    void deleteFAQ(UUID uuid);
    void reorderFAQs(List<UUID> orderedUuids);
    void moveFAQsBetweenCategories(UUID oldCategoryUuid, UUID newCategoryUuid, UUID updatedBy);
    FAQResponseDTO unlinkFAQ(UUID uuid);
    // ================================
    // VALIDATION METHODS
    // ================================

    // ================================
    // ANALYTICS AND STATISTICS
    // ================================

}
