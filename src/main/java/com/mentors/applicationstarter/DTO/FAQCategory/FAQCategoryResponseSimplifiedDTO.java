package com.mentors.applicationstarter.DTO.FAQCategory;

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
public class FAQCategoryResponseSimplifiedDTO {
    private Long id;
    private UUID uuid;
    private String name;
    private String description;
    private String iconClass;
    private String colorCode;
    private Integer displayOrder;
    private Boolean isActive;
    private Boolean isVisible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;

}
