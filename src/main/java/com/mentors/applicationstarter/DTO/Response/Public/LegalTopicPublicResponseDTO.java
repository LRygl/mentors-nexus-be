package com.mentors.applicationstarter.DTO.Response.Public;

import com.mentors.applicationstarter.DTO.Response.Admin.LegalSectionAdminResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LegalTopicPublicResponseDTO {
    private Long id;
    private UUID uuid;
    private String name;
    private String subtitle;
    private Instant createdAt;
    private UUID createdBy;
    private Instant updatedAt;
    private UUID updatedBy;
    private Instant effectiveAt;
    private Instant publishedAt;
    private UUID publishedBy;
    private Boolean published;
    private Boolean showCta;
    private String footer;
    private List<LegalSectionAdminResponseDTO> sections;
}
