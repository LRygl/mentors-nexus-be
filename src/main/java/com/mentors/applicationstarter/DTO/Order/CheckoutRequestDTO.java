package com.mentors.applicationstarter.DTO.Order;

import com.mentors.applicationstarter.Enum.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for the checkout process.
 * Handles both authenticated and new user checkout flows.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequestDTO {

    // Cart items - list of course IDs to purchase
    private List<Long> courseIds;

    // User details (used for registration if new user, or profile update if existing)
    private String firstName;
    private String lastName;
    private String email;
    private String telephoneNumber;

    // Only for new account registration (null if user is already logged in)
    private String password;

    // Billing address
    private String street;
    private String city;
    private String postalCode;
    private String country;

    // Payment method selection (no actual processing yet)
    private PaymentMethod selectedPaymentMethod;

    // Consent flags
    private Boolean termsAccepted;
    private Boolean privacyAccepted;
    private Boolean marketingConsent;
}
