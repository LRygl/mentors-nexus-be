package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceImmutableException;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.Response.HttpResponse;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Repository.UserRepository;
import com.mentors.applicationstarter.Service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @Value("${allowPublicUserRegistration}")
    private Boolean allowPublicUserRegistration;

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> register(@RequestBody User user, HttpServletRequest request) throws Exception {
        if(allowPublicUserRegistration) {
            return authenticationService.handleUserRegistrationRequest(user, request);
        } else {
            throw new ResourceImmutableException(ErrorCodes.REGISTRATION_NOT_ALLOWED);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<User> authenticate(@RequestBody User authenticateUser) {
        String jwtToken = authenticationService.authenticate(authenticateUser);
        Optional<User> loggedInUser = userRepository.findByEmail(authenticateUser.getEmail());
        LOGGER.info("User login request received");
        if (loggedInUser.isPresent()) {
            HttpHeaders jwtHeader = new HttpHeaders();
            jwtHeader.add("X-JWT-TOKEN", jwtToken);

            return new ResponseEntity<>(loggedInUser.get(), jwtHeader, HttpStatus.OK);
        } else {
            // Handle the case where the user is not found, for example, return 404 Not Found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<HttpResponse> requestResetPassword(@RequestBody User user) throws MessagingException, IOException, ResourceNotFoundException {
        HttpResponse response = authenticationService.requestUserPasswordReset(user.getEmail());
        return new ResponseEntity<>(response, null, response.getHttpStatusCode());
    }

    @GetMapping("/activate")
    public ResponseEntity<HttpResponse> validateUserEmailAddress(@RequestParam String activationId) throws MessagingException, IOException {
        HttpResponse response = authenticationService.activateNewUser(activationId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getHttpStatusCode()));
    }

    @GetMapping("/confirm-password-reset")
    public ResponseEntity<HttpResponse> confirmResetPassword(@RequestParam("operationId") String operationId, @RequestParam("userId") String userId) throws MessagingException, IOException {
        UUID decodedOperationUUID = UUID.fromString(new String(Base64.getUrlDecoder().decode(operationId)));
        UUID decodedUserUUID = UUID.fromString(new String(Base64.getUrlDecoder().decode(userId)));
        return ResponseEntity.ok(authenticationService.confirmUserPasswordReset(decodedOperationUUID, decodedUserUUID));
    }

}
