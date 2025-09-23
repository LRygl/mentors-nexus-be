package com.mentors.applicationstarter.DTO.FAQ;

import com.mentors.applicationstarter.Enum.FAQPriority;
import com.mentors.applicationstarter.Enum.FAQStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FAQSimplifiedResponseDTO {
    private Long id;
    private UUID uuid;
    private String question;
    private String answer;
    private FAQStatus status;
    private Integer displayOrder;
    private Boolean isPublished;
    private Boolean isFeatured;
    private FAQPriority priority;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UUID createdBy;
    private UUID updatedBy;
}
