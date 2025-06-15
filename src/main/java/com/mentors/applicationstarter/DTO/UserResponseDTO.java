package com.mentors.applicationstarter.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;
    private UUID UUID;
    private String firstName;
    private String lastName;
    private String email;
    private String telephoneNumber;
    private Date lastLoginDateDisplay;
    private Date registerDate;
    private Date lastUpdatedDate;

    private Boolean isAccountNonLocked;
    private Boolean forcePasswordChangeOnLogin;

    private Boolean personalDataProcessing;
    private Boolean personalDataPublishing;
    private Boolean marketing;
    private Boolean cookiePolicyConsent;

    private String role;

    // To avoid recursion and infinite loops:
    private Set<CourseSummaryDTO> ownedCourses;
    private Set<CourseSummaryDTO> joinedCourses;
}
