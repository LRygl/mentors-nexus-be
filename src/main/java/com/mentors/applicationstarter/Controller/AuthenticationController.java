package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.DTO.UserResponseDTO;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceImmutableException;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Mapper.UserMapper;
import com.mentors.applicationstarter.Model.Response.HttpResponse;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Repository.UserRepository;
import com.mentors.applicationstarter.Service.AuthenticationService;
import com.mentors.applicationstarter.Service.CookieService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;
    private final CookieService cookieService;
    private final UserRepository userRepository;



    @Value("${allowPublicUserRegistration}")
    private Boolean allowPublicUserRegistration;

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(
            @RequestBody User user,
            HttpServletRequest request
    ) throws Exception {
        if (allowPublicUserRegistration) {
            return authenticationService.handleUserRegistrationRequest(user, request);
        } else {
            throw new ResourceImmutableException(ErrorCodes.REGISTRATION_NOT_ALLOWED);
        }
    }

    /**
     * Login endpoint - sets JWT tokens as HttpOnly cookies
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticate(
            @RequestBody User authenticateUser,
            HttpServletResponse response
    ) {
        LOGGER.info("User login request received for: {}", authenticateUser.getEmail());
        // Authenticate and set cookies
        Map<String, Object> authResponse = authenticationService.authenticate(authenticateUser, response);

        return ResponseEntity.ok(authResponse);
    }

    /**
     * Refresh access token using refresh token cookie
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ResourceNotFoundException {
        LOGGER.info("Token refresh request received");

        authenticationService.refreshAccessToken(request, response);
        return ResponseEntity.ok(Map.of(
                "message", "Token refreshed successfully"
        ));
    }



    /**
     * Logout endpoint - clears cookies
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        LOGGER.info("User logout request received");

        // Delete all auth cookies
        cookieService.deleteAllAuthCookies(response);

        return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully"
        ));
    }

    /**
     * Get current user - validates cookie automatically via filter
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(HttpServletRequest request) throws ResourceNotFoundException {
        // JWT filter already authenticated the user
        // Extract email from security context
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.USER_DOES_NOT_EXIST));

        UserResponseDTO responseUser = UserMapper.mapUserToDto(user);
        return ResponseEntity.ok(responseUser);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<HttpResponse> requestResetPassword(@RequestBody User user) throws MessagingException, IOException, ResourceNotFoundException {
        HttpResponse response = authenticationService.requestUserPasswordReset(user.getEmail());
        return new ResponseEntity<>(response, null, response.getHttpStatusCode());
    }

    //TODO Store tokens in DB
    @GetMapping("/activate")
    public ResponseEntity<HttpResponse> validateUserEmailAddress(@RequestParam String token) throws MessagingException, IOException {
        HttpResponse response = authenticationService.activateNewUser(token);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getHttpStatusCode()));
    }

    @GetMapping("/confirm-password-reset")
    public ResponseEntity<HttpResponse> confirmResetPassword(@RequestParam("operationId") String operationId, @RequestParam("userId") String userId) throws MessagingException, IOException {
        UUID decodedOperationUUID = UUID.fromString(new String(Base64.getUrlDecoder().decode(operationId)));
        UUID decodedUserUUID = UUID.fromString(new String(Base64.getUrlDecoder().decode(userId)));
        return ResponseEntity.ok(authenticationService.confirmUserPasswordReset(decodedOperationUUID, decodedUserUUID));
    }

}
