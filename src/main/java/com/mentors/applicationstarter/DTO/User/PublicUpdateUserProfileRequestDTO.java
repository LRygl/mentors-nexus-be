package com.mentors.applicationstarter.DTO.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        name = "PublicUpdateUserProfileRequest",
        description = "Request payload for updating user profile data. " +
                "All fields are optional. Only non-null fields will be updated."
)
public class PublicUpdateUserProfileRequestDTO {
    // Personal info fields - all optional, only update if provided
    private String firstName;
    private String lastName;
    private String telephoneNumber;

    // Billing address fields - all optional, only update if provided
    private String billingFirstName;
    private String billingLastName;
    private String billingStreet;
    private String billingCity;
    private String billingPostalCode;
    private String billingCountry;

    // Consent fields - all optional, only update if provided
    private Boolean personalDataProcessing;
    private Boolean personalDataPublishing;
    private Boolean marketing;
    private Boolean cookiePolicyConsent;
}
