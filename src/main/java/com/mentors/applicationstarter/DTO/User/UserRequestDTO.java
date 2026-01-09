package com.mentors.applicationstarter.DTO.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {
    private String telephoneNumber;
    private Boolean personalDataProcessing;
    private Boolean personalDataPublishing;
    private Boolean marketing;
    private Boolean cookiePolicyConsent;
    private String lightBg;
    private String darkBg;

    private String role;
}
