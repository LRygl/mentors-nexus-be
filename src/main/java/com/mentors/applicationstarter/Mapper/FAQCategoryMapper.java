package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.FAQCategory.FAQCategoryPageResponseDTO;
import com.mentors.applicationstarter.DTO.FAQCategory.FAQCategorySimplifiedDTO;
import com.mentors.applicationstarter.Model.FAQCategory;

public class FAQCategoryMapper {

    public static FAQCategoryPageResponseDTO toFAQCategoryPageResponseDto(FAQCategory faqCategory) {
        if (faqCategory == null) return null;

        return FAQCategoryPageResponseDTO.builder()
                .id(faqCategory.getId())
                .uuid(faqCategory.getUuid())
                .name(faqCategory.getName())
                .description(faqCategory.getDescription())
                .slug(faqCategory.getSlug())
                .iconClass(faqCategory.getIconClass())
                .colorCode(faqCategory.getColorCode())
                .displayOrder(faqCategory.getDisplayOrder())
                .isActive(faqCategory.getIsActive())
                .isVisible(faqCategory.getIsVisible())
                .metaDescription(faqCategory.getMetaDescription())
                .metaKeywords(faqCategory.getMetaKeywords())
                .faqCount(faqCategory.getFaqCount())
                .publishedFaqCount(faqCategory.getPublishedFaqCount())
                .createdAt(faqCategory.getCreatedAt())
                .updatedAt(faqCategory.getUpdatedAt())
                .createdBy(faqCategory.getCreatedBy())
                .updatedBy(faqCategory.getUpdatedBy())
                .build();
    }

    public static FAQCategorySimplifiedDTO toFAQCategorySimplifedResponseDTO(FAQCategory faqCategory) {
        if (faqCategory == null) return null;

        return FAQCategorySimplifiedDTO.builder()
                .id(faqCategory.getId())
                .uuid(faqCategory.getUuid())
                .name(faqCategory.getName())
                .description(faqCategory.getDescription())
                .iconClass(faqCategory.getIconClass())
                .colorCode(faqCategory.getColorCode())
                .displayOrder(faqCategory.getDisplayOrder())
                .isActive(faqCategory.getIsActive())
                .isVisible(faqCategory.getIsVisible())
                .createdAt(faqCategory.getCreatedAt())
                .updatedAt(faqCategory.getUpdatedAt())
                .createdBy(faqCategory.getCreatedBy())
                .updatedBy(faqCategory.getUpdatedBy())
                .build();
    }

}
