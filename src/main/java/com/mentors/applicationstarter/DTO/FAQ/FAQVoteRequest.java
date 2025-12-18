package com.mentors.applicationstarter.DTO.FAQ;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FAQVoteRequest {
    private boolean helpful;
}
