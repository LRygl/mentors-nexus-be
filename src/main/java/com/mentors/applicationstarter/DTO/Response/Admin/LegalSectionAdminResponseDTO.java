package com.mentors.applicationstarter.DTO.Response.Admin;

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
public class LegalSectionAdminResponseDTO {
    private Long id;
    private UUID uuid;
    private UUID createdBy;
    private UUID updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
    private String name;
    private String icon;
    private Integer orderIndex;
    private List<LegalItemAdminResponseDTO> items;

}
