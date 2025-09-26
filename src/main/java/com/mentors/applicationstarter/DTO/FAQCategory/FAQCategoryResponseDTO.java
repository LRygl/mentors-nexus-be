package com.mentors.applicationstarter.DTO.FAQCategory;

import com.mentors.applicationstarter.DTO.FAQ.FAQResponseSimplifiedDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FAQCategoryResponseDTO {
    private Long id;
    private UUID uuid;
    private String name;
    private String description;
    private String slug;
    private String iconClass;
    private String colorCode;
    private Integer displayOrder;
    private Boolean isActive;
    private Boolean isVisible;
    private String metaDescription;
    private String metaKeywords;

    private Integer faqCount;
    private Integer publishedFaqCount;
    private List<FAQResponseSimplifiedDTO> faqs;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;

    private String displayName;
    private String fullUrl;
}
