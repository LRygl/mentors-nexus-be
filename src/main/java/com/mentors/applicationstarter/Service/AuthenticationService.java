package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.Model.Response.HttpResponse;
import com.mentors.applicationstarter.Model.User;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public interface AuthenticationService {
    ResponseEntity<HttpResponse> handleUserRegistrationRequest(User registeredUser, HttpServletRequest request) throws Exception;
    String authenticate(User authenticateUser);

    void requestUserPasswordReset(String email) throws IOException, MessagingException;
    HttpResponse confirmUserPasswordReset(UUID operationId, UUID userId) throws MessagingException, IOException;
    HttpResponse activateNewUser(String activationString) throws MessagingException, IOException;

    void createAdminUser() throws IOException;

}
