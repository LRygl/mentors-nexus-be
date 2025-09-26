package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.FAQCategory.FAQCategoryResponseDTO;
import com.mentors.applicationstarter.DTO.FAQCategory.FAQCategoryResponseSimplifiedDTO;
import com.mentors.applicationstarter.Model.FAQCategory;

import java.util.stream.Collectors;

public class FAQCategoryMapper {

    public static FAQCategoryResponseDTO toFAQCategoryResponseDTO(FAQCategory faqCategory) {
        if (faqCategory == null) return null;

        return FAQCategoryResponseDTO.builder()
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
                .faqs(faqCategory.getFaqs().stream()
                        .map(FAQMapper::toFaqSimplifiedResponseDto)
                        .collect(Collectors.toList())
                )
                .createdAt(faqCategory.getCreatedAt())
                .updatedAt(faqCategory.getUpdatedAt())
                .createdBy(faqCategory.getCreatedBy())
                .updatedBy(faqCategory.getUpdatedBy())
                .build();
    }

    public static FAQCategoryResponseSimplifiedDTO toFAQCategorySimplifedResponseDTO(FAQCategory faqCategory) {
        if (faqCategory == null) return null;

        return FAQCategoryResponseSimplifiedDTO.builder()
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
