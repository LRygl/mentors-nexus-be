package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.FAQ.FAQResponseDTO;
import com.mentors.applicationstarter.DTO.FAQ.FAQSimplifiedResponseDTO;
import com.mentors.applicationstarter.DTO.FAQCategory.FAQCategorySimplifiedDTO;
import com.mentors.applicationstarter.Model.FAQ;

public class FAQMapper {

    public static FAQResponseDTO toFaqResponseDto(FAQ faq) {
        if (faq == null) return null;

        return FAQResponseDTO.builder()
                .id(faq.getId())
                .uuid(faq.getUuid())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .category(FAQCategoryMapper.toFAQCategorySimplifedResponseDTO(faq.getCategory()))
                .status(faq.getStatus())
                .displayOrder(faq.getDisplayOrder())
                .isPublished(faq.getIsPublished())
                .isFeatured(faq.getIsFeatured())
                .searchKeywords(faq.getSearchKeywords())
                .metaDescription(faq.getMetaDescription())
                .slug(faq.getSlug())
                .viewCount(faq.getViewCount())
                .helpfulVotes(faq.getHelpfulVotes())
                .notHelpfulVotes(faq.getNotHelpfulVotes())
                .priority(faq.getPriority())
                .createdAt(faq.getCreatedAt())
                .updatedAt(faq.getUpdatedAt())
                .createdBy(faq.getCreatedBy())
                .updatedBy(faq.getUpdatedBy())
                .build();
    }

    public static FAQSimplifiedResponseDTO toFaqSimplifiedResponseDtop(FAQ faq) {
        if (faq == null) return null;

        return FAQSimplifiedResponseDTO.builder()
                .id(faq.getId())
                .uuid(faq.getUuid())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .status(faq.getStatus())
                .displayOrder(faq.getDisplayOrder())
                .isPublished(faq.getIsPublished())
                .isFeatured(faq.getIsFeatured())
                .priority(faq.getPriority())
                .createdAt(faq.getCreatedAt())
                .updatedAt(faq.getUpdatedAt())
                .createdBy(faq.getCreatedBy())
                .updatedBy(faq.getUpdatedBy())
                .build();
    }
}
