package com.mentors.applicationstarter.DTO;

import com.mentors.applicationstarter.Enum.FAQPriority;
import com.mentors.applicationstarter.Enum.FAQStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FAQRequest {
    private String question;
    private String answer;
    private Long categoryId;
    private FAQStatus status;
    private Integer displayOrder;
    private Boolean isPublished;
    private Boolean isFeatured;
    private String searchKeywords;
    private String metaDescription;
    private FAQPriority priority;
}