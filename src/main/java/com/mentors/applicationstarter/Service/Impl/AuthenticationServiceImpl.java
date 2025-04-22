package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Enum.EventCategory;
import com.mentors.applicationstarter.Enum.EventType;
import com.mentors.applicationstarter.Exception.ResourceAlreadyExistsException;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.Response.HttpResponse;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Repository.UserRepository;
import com.mentors.applicationstarter.Service.AuthenticationService;
import com.mentors.applicationstarter.Service.EventService;
import com.mentors.applicationstarter.Service.JwtService;
import com.mentors.applicationstarter.Enum.Role;
import com.mentors.applicationstarter.Utils.Base64Utils;
import com.mentors.applicationstarter.Utils.EmailServiceUtils;
import com.mentors.applicationstarter.Utils.HttpResponseFactory;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static com.mentors.applicationstarter.Constant.ApplicationConstant.APP_EXPIRY_DURATION_24H;
import static com.mentors.applicationstarter.Constant.ApplicationConstant.APP_URL;
import static com.mentors.applicationstarter.Constant.EmailServiceConstant.*;
import static com.mentors.applicationstarter.Constant.EventConstant.EVENT_AUTH_USER_REGISTERED;
import static com.mentors.applicationstarter.Constant.FileConstant.USER_FOLDER;
import static com.mentors.applicationstarter.Constant.SecurityConstant.PASSOWRD_GENERATOR_STRING;
import static com.mentors.applicationstarter.Constant.SecurityConstant.PASSWORD_GENERATOR_LENGTH;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailServiceUtils emailServiceUtils;
    private final EventService eventService;
    private final Base64Utils base64Utils;

    //TODO Load from AWS PARAMETER STORE
    @Value("${generateUserPasswordOnRegistration}")
    private Boolean generateUserPasswordOnRegistration;

    @Value("${applicationAdminName}")
    private String applicationAdminName;

    @Value("${applicationAdminEmail}")
    private String applicationAdminEmail;

    @Value("${applicationAdminPhone}")
    private String applicationAdminPhone;

    @Value("${reguireRegisteredUserEmailConfirmation}")
    private Boolean reguireRegisteredUserEmailConfirmation;

    @Value("${reguireRegisteredUserAdminApproval}")
    private Boolean reguireRegisteredUserAdminApproval;

    private Boolean forcePasswordResetOnLogin;

    @Override
    public ResponseEntity<HttpResponse> handleUserRegistrationRequest(User registeredUser, HttpServletRequest request) throws ResourceAlreadyExistsException, IOException {
        String passwordGenerationStrategy = "useProvidedPassword";
        boolean requireUserEmailConfirmation = false;

        if (userExistsByEmail(registeredUser.getEmail())) {
            throw new ResourceAlreadyExistsException(ErrorCodes.USER_ALREADY_REGISTERED, registeredUser.getEmail());
        }

        //Could be moved to saveUser() directly and map the whole user class?
        User user = User.builder()
                .firstName(registeredUser.getFirstName())
                .lastName(registeredUser.getLastName())
                .email(registeredUser.getEmail())
                .telephoneNumber(registeredUser.getTelephoneNumber())
                .UUID(UUID.randomUUID())
                .isAccountNonLocked(false)
                .registerDate(new Date())
                .role(Role.USER)
                .marketing(registeredUser.getMarketing())
                .personalDataProcessing(registeredUser.getPersonalDataProcessing())
                .personalDataPublishing(registeredUser.getPersonalDataPublishing())
                .build();

            //Process registration based on the selected password generation strategy
            switch (passwordGenerationStrategy) {
                case "generateUserPasswordsStrategy":
                    String generatedSecurePassword = generateRandomUserPassword();
                    user.setPassword(encryptAndSaltUserPassword(generatedSecurePassword));

                    //If application forces password change - create user with this flag
                    if(forcePasswordResetOnLogin){
                        user.setForcePasswordChangeOnLogin(true);
                    }
                    //Activate user account
                    user.setIsAccountNonLocked(true);
                    //Send email with password
                    Map<String, String> generatePasswordTemplateVariables = new HashMap<>();
                    generatePasswordTemplateVariables.put("userEmail", user.getEmail());
                    generatePasswordTemplateVariables.put("userLoginPassword", generatedSecurePassword);
                    emailServiceUtils.sendEmail(
                            user.getEmail(),
                            MAIL_APPLICATION_SUBJECT_NAME + MAIL_SUBJECT_REGISTER_NEW_USER,
                            generatePasswordTemplateVariables,
                            MAIL_TEMPLATE_REGISTER_NEW_USER_PASSWORD
                    );

                    saveUser(user);
                    break;

                case "useProvidedPassword":
                    if(requireUserEmailConfirmation) {
                        //Lock user account until it is activated
                        user.setIsAccountNonLocked(false);
                        //Generate activation URL for this user
                        Instant timestamp = Instant.now();
                        String userAccountActivationString = String.format("%s+%s", user.getUUID(), timestamp);
                        String base64EncodedActivationString = base64Utils.encodeStringToUrlSafeBase64(userAccountActivationString);
                        String userAccountActivationUrl = String.format("%s/%s%s", APP_URL,"auth/activate?activationId=", base64EncodedActivationString);
                        //TODO
                        //Send email with password and confirmation link - account remains blocked until activation link is used - separate method + endpoint
                        Map<String, String> providedPasswordTemplateVariables = new HashMap<>();
                        providedPasswordTemplateVariables.put("userEmail", user.getEmail());
                        providedPasswordTemplateVariables.put("userActivationUrl", userAccountActivationUrl);
                        emailServiceUtils.sendEmail(user.getEmail(), MAIL_APPLICATION_SUBJECT_NAME + MAIL_SUBJECT_REGISTER_NEW_USER, providedPasswordTemplateVariables, MAIL_TEMPLATE_REGISTER_PROVIDED_USER_PASSWORD_ACTIVATION);
                    } else {
                        //Activate user account
                        user.setIsAccountNonLocked(true);
                        //Send email with password
                        Map<String, String> providedPasswordTemplateVariables = new HashMap<>();
                        providedPasswordTemplateVariables.put("userEmail", user.getEmail());
                        emailServiceUtils.sendEmail(user.getEmail(), MAIL_APPLICATION_SUBJECT_NAME + MAIL_SUBJECT_REGISTER_NEW_USER, providedPasswordTemplateVariables, MAIL_TEMPLATE_REGISTER_PROVIDED_USER_PASSWORD);
                    }
                    user.setPassword(encryptAndSaltUserPassword(registeredUser.getPassword()));
                    saveUser(user);
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + passwordGenerationStrategy);
            }

            HttpResponse response = HttpResponseFactory.created(
                    "User created successfully",
                    "The user with email " + user.getEmail() + " has been created.",
                    Map.of("user", user)
            );
        generateUserFolder(user.getUUID());
        eventService.generateEvent(user.getUUID(),EVENT_AUTH_USER_REGISTERED ,user.getEmail(),EventCategory.USER, EventType.REGISTRATION,this.getClass().getSimpleName());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Transactional
    private boolean saveUser(User user) {
        try {
            userRepository.save(user);
            LOGGER.info("User {} with id {} was successfully saved to to the database.", user.getId(),user.getUUID());
            return user.getId() != null;
        } catch (Exception e) {
            LOGGER.error("USER WAS NOT SAVED TO DATABASE!");
            return false;
        }
    }

    @Override
    public String authenticate(User authenticateUser) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticateUser.getEmail(),
                        authenticateUser.getPassword()
                )
        );
        var user = userRepository.findByEmail(authenticateUser.getEmail()).orElseThrow();
        user.setLastLoginDateDisplay(user.getLastLoginDate());
        user.setLastLoginDate(new Date());
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        eventService.generateEvent(user.getUUID(),"User Authentication Request",user.getEmail(),EventCategory.USER,EventType.AUTH,this.getClass().getSimpleName());

        return jwtToken;
    }

    @Override
    public HttpResponse requestUserPasswordReset(String email) throws ResourceNotFoundException {
        Instant resetLimit = Instant.now().plus(Duration.ofHours(APP_EXPIRY_DURATION_24H));
        UUID operationID = UUID.randomUUID();

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException(ErrorCodes.USER_DOES_NOT_EXIST));
        LOGGER.debug("Password reset request processed - user found");
        user.setPasswordResetOperationUUID(operationID);
        user.setPasswordResetExpiryDate(Date.from(resetLimit));
        userRepository.save(user);

        String encodedOperationId = base64Utils.encodeStringToUrlSafeBase64(String.valueOf(operationID));
        String encodedUserId = base64Utils.encodeStringToUrlSafeBase64(user.getUUID().toString());
        String passwordResetLinkString = MAIL_APPLICATION_BASE_URL + "?operationId=" + encodedOperationId + "&userId=" + encodedUserId;

        Map<String, String> templateVariables = new HashMap<>();
        templateVariables.put("userEmail", user.getEmail());
        templateVariables.put("userPasswordResetLink", passwordResetLinkString);
        emailServiceUtils.sendEmail(user.getEmail(), MAIL_APPLICATION_SUBJECT_NAME + MAIL_SUBJECT_PASSWORD_RESET_REQUEST, templateVariables, MAIL_TEMPLATE_RESET_USER_PASSWORD_REQUEST);

        userRepository.save(user);

        return HttpResponseFactory.ok(
                "Password Change request processed",
                "Password change request for " + user.getEmail(),
                Map.of("user", user)
        );
    }

    public HttpResponse confirmUserPasswordReset(UUID operationId, UUID userId) {
        return userRepository.findByUUID(userId)
                .map(user -> {
                    Instant expiryDate = user.getPasswordResetExpiryDate().toInstant();
                    Instant now = Instant.now();
                    if(now.isBefore(expiryDate) && operationId.equals(user.getPasswordResetOperationUUID())) {
                        String password = generateRandomUserPassword();
                        LOGGER.debug("New user password generated: {}", password);
                        user.setPassword(encryptAndSaltUserPassword(password));
                        user.setPasswordResetExpiryDate(null);
                        user.setPasswordResetOperationUUID(null);
                        if(forcePasswordResetOnLogin){
                            user.setForcePasswordChangeOnLogin(true);
                        }
                        userRepository.save(user);

                        Map<String, String> templateVariables = new HashMap<>();
                        templateVariables.put("userEmail", user.getEmail());
                        templateVariables.put("newUserPassword", password);
                        emailServiceUtils.sendEmail(user.getEmail(), MAIL_APPLICATION_SUBJECT_NAME + MAIL_SUBJECT_PASSWORD_RESET_NEW_PASSWORD, templateVariables, MAIL_TEMPLATE_PASSWORD_RESET_NEW_PASSWORD);

                        return HttpResponseFactory.ok(
                                "Password change request was confirmed",
                                "Password reset OK",
                                Map.of("user", user)
                        );
                    } else {
                        return HttpResponseFactory.notFound(
                                "Time Expired",
                                "Token validity expired. Expiry: " + expiryDate + ", Now: " + now + " or password reset ID: " + operationId +" was not found. Request is invalid."
                        );
                    }
                })
                .orElse(HttpResponseFactory.notFound(
                        "User password reset request not processed - User was not found",
                        "ERROR: UserID: " + userId + " was not found. Request is invalid."
                ));
    }

    @Override
    public HttpResponse activateNewUser(String activationString) throws MessagingException, IOException {
        //TODO implement user activation - this action should be only accessible by the admin
        String base64DecodedString = base64Utils.decodeUrlSafeBase64ToString(activationString);
        LOGGER.debug("Activation stirng - {} ", base64DecodedString);
        String[] securityString = base64DecodedString.split("\\+",2);
        String activationUUID = securityString[0];
        String activationTimestamp = securityString[1];
        eventService.generateEvent(UUID.fromString(activationUUID),"User Account Activated",base64DecodedString,EventCategory.USER,EventType.ACTIVATION,this.getClass().getSimpleName());

        Instant timestamp = Instant.parse(activationTimestamp);
        Instant now = Instant.now();
        Instant threshold = now.minus(Duration.ofHours(24));
        //TODO change for TimeUtils
        if (timestamp.isAfter(threshold)) {
            return userRepository.findByUUID(UUID.fromString(activationUUID))
                    .map(existingUser -> {
                        activateUserAccount(existingUser);
                        userRepository.save(existingUser);

                        return HttpResponseFactory.ok(
                                "User activation request accepted",
                                "Activation request for " + existingUser.getEmail() + " based on request " + Arrays.toString(securityString),
                                Map.of("user", existingUser)
                        );
                    })
                    .orElseGet(() -> HttpResponse.builder()
                            .httpTimestamp(new Date())
                            .httpStatusCode(HttpStatus.NOT_FOUND.value())
                            .httpStatus(HttpStatus.NOT_FOUND)
                            .httpStatusReason(HttpStatus.NOT_FOUND.getReasonPhrase())
                            .httpStatusMessage("User not found")
                            .httpDeveloperMessage("User not found with UUID: " + activationUUID)
                            .build());
        }

        return HttpResponseFactory.badRequest(
                "Activation link expired",
                "Activation request is older than 24 hours"
        );

    }


    @Override
    public void createAdminUser() throws IOException {
        Optional<User> adminUser = userRepository.findById(1L);
        if (adminUser.isEmpty()) {
            User user = new User();
            user.setUUID(UUID.randomUUID());
            user.setFirstName(applicationAdminName);
            user.setLastName("Admin");
            user.setEmail(applicationAdminEmail);
            user.setTelephoneNumber(applicationAdminPhone);
            user.setPassword(passwordEncoder.encode("admin"));
            user.setRegisterDate(new Date());
            user.setIsAccountNonLocked(true);
            user.setRole(Role.ROLE_ADMIN);

            userRepository.save(user);
            eventService.generateEvent(user.getUUID(),"New User Registered",user.getEmail(),EventCategory.USER,EventType.REGISTRATION,this.getClass().getSimpleName());
            generateUserFolder(user.getUUID());

        }

    }
    /* -------------------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------- PRIVATE METHODS ----------------------------------------------------- */
    /* -------------------------------------------------------------------------------------------------------------- */

    private Boolean userExistsByEmail(String userEmail) {
        Optional<User> user = userRepository.findByEmail((userEmail));
        return userRepository.findByEmail(userEmail).isPresent();
    }

    public void activateUserAccount(User user) {
        user.setIsAccountNonLocked(true);
        userRepository.save(user);
    }

    public void deactivateUserAccount(User user) {
        user.setIsAccountNonLocked(false);
        userRepository.save(user);
    }

    private String generateRandomUserPassword() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder securePassword = new StringBuilder();

        for (int i = 0; i < PASSWORD_GENERATOR_LENGTH; i++) {
            int index = secureRandom.nextInt(PASSOWRD_GENERATOR_STRING.length());
            securePassword.append(PASSOWRD_GENERATOR_STRING.charAt(index));
        }
        return securePassword.toString();
    }

    private String encryptAndSaltUserPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void generateUserFolder(UUID userUUID) throws IOException {
        Path userFolder = Paths.get(USER_FOLDER + userUUID).toAbsolutePath().normalize();
        Files.createDirectories(userFolder);
    }

}
