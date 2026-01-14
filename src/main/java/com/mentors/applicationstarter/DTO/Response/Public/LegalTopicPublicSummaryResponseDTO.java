package com.mentors.applicationstarter.DTO.Response.Public;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LegalTopicPublicSummaryResponseDTO {
    private Long id;
    private String name;
    private String subtitle;
    private Instant createdAt;
    private Instant effectiveAt;

}
