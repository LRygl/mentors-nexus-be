package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.Exception.ResourceAlreadyExistsException;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.Response.HttpResponse;
import com.mentors.applicationstarter.Model.User;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public interface AuthenticationService {
    ResponseEntity<HttpResponse> handleUserRegistrationRequest(
            User registeredUser,
            HttpServletRequest request
    ) throws ResourceAlreadyExistsException, IOException;

    /**
     * UPDATED: Now returns Map<String, Object> and sets cookies via response
     */
    Map<String, Object> authenticate(User authenticateUser, HttpServletResponse response);

    /**
     * NEW: Refresh access token using refresh token from cookie
     */
    void refreshAccessToken(HttpServletRequest request, HttpServletResponse response)
            throws ResourceNotFoundException;

    HttpResponse requestUserPasswordReset(String email) throws ResourceNotFoundException;

    HttpResponse confirmUserPasswordReset(UUID operationId, UUID userId);

    HttpResponse activateNewUser(String activationString) throws MessagingException, IOException;

    void createAdminUser() throws IOException;

}
