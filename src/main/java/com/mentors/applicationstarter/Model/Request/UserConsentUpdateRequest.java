package com.mentors.applicationstarter.Model.Request;

import lombok.Data;
import lombok.Getter;

@Data
public class UserConsentUpdateRequest {
    private Boolean cookiePolicyConsent;
    private Boolean personalDataProcessingConsent;
    private Boolean personalDataPublishingConsent;
    private Boolean marketingConsent;
}
