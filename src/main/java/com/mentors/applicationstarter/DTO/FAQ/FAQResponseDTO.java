package com.mentors.applicationstarter.DTO.FAQ;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mentors.applicationstarter.DTO.FAQCategory.FAQCategorySimplifiedDTO;
import com.mentors.applicationstarter.Enum.FAQPriority;
import com.mentors.applicationstarter.Enum.FAQStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FAQResponseDTO {
    private Long id;
    private UUID uuid;
    private String question;
    private String answer;
    private FAQCategorySimplifiedDTO category;
    private FAQStatus status;
    private Integer displayOrder;
    private Boolean isPublished;
    private Boolean isFeatured;
    private String searchKeywords;
    private String metaDescription;
    private String slug;
    private Long viewCount;
    private Integer helpfulVotes;
    private Integer notHelpfulVotes;
    private FAQPriority priority;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UUID createdBy;
    private UUID updatedBy;
}
