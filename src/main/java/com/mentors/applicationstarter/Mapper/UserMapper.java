package com.mentors.applicationstarter.Mapper;

import com.mentors.applicationstarter.DTO.UserResponseDTO;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Repository.CourseEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final CourseEnrollmentRepository enrollmentRepository;

    public UserResponseDTO mapUserToDto(User user) {

        Set<Long> enrolledCourseIds = enrollmentRepository.findCourseIdsByUserId(user.getId());

        return UserResponseDTO.builder()
                .id(user.getId())
                .UUID(user.getUUID())
                .email(user.getEmail())
                .telephoneNumber(user.getTelephoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRoleName())
                .enrolledCourseIds(user.getEnrolledCourseIds())
                // Billing - now matches actual entity field names
                .billingFirstName(user.getBillingFirstName())
                .billingLastName(user.getBillingLastName())
                .billingStreet(user.getBillingStreet())
                .billingCity(user.getBillingCity())
                .billingPostalCode(user.getBillingPostalCode())
                .billingCountry(user.getBillingCountry())
                // Consent - were missing entirely
                .personalDataProcessing(user.getPersonalDataProcessing())
                .personalDataPublishing(user.getPersonalDataPublishing())
                .marketing(user.getMarketing())
                .cookiePolicyConsent(user.getCookiePolicyConsent())
                .build();
    }

}
