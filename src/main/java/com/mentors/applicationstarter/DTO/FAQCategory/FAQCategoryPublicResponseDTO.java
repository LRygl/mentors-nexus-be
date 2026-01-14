package com.mentors.applicationstarter.DTO.FAQCategory;

import com.mentors.applicationstarter.DTO.FAQ.FAQResponseSimplifiedDTO;
import com.mentors.applicationstarter.Model.FAQ;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FAQCategoryPublicResponseDTO {
    private Long id;
    private UUID uuid;
    private String name;
    private String description;
    private String iconClass;
    private String colorCode;
    private Integer displayOrder;
    private Boolean isActive;
    private List<FAQResponseSimplifiedDTO> faqs;
}
