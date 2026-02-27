package com.mentors.applicationstarter.Controller.Public;

import com.mentors.applicationstarter.DTO.User.PublicUpdateUserProfileRequestDTO;
import com.mentors.applicationstarter.DTO.UserResponseDTO;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Service.UserService;
import com.mentors.applicationstarter.Utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/profile")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Slf4j
public class UserProfileController {

    private final UserService userService;


    @PutMapping
    @Operation(
            summary = "Update user profile",
            description = "Updates the authenticated user's profile. " +
                    "Only non-null fields from the request will be applied."
    ) public ResponseEntity<UserResponseDTO> updateUserProfile(
            @AuthenticationPrincipal User authenticatedUser,
            @RequestBody @Valid PublicUpdateUserProfileRequestDTO publicUpdateUserProfileRequestDTO
    ) {

        return new ResponseEntity<>(userService.updateUserProfile(
                authenticatedUser.getId(),
                publicUpdateUserProfileRequestDTO),
                HttpStatus.OK
        );
    }
}
