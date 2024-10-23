package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.ResourceAlreadyExistsException;
import com.mentors.applicationstarter.Model.Response.HttpResponse;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Repository.UserRepository;
import com.mentors.applicationstarter.Service.AuthenticationService;
import com.mentors.applicationstarter.Service.JwtService;
import com.mentors.applicationstarter.Enum.Role;
import com.mentors.applicationstarter.Utils.EmailServiceUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.*;

import static com.mentors.applicationstarter.Constant.EmailServiceConstant.*;
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

    @Override
    public ResponseEntity<HttpResponse> register(User registerUser, HttpServletRequest request) throws Exception {
        if(userExistsByEmail(registerUser.getEmail())) {
            throw new ResourceAlreadyExistsException(ErrorCodes.USER_ALREADY_REGISTERED, registerUser.getEmail());
        }

        User newUser = new User();
        newUser.setFirstName(registerUser.getFirstName());
        newUser.setLastName(registerUser.getLastName());
        newUser.setEmail(registerUser.getEmail());
        newUser.setTelephoneNumber(registerUser.getTelephoneNumber());
        newUser.setUUID(UUID.randomUUID());
        newUser.setIsAccountNonLocked(false);
        newUser.setRegisterDate(new Date());
        newUser.setRole(Role.USER);

        newUser.setMarketing(registerUser.getMarketing());
        newUser.setPersonalDataProcessing(registerUser.getPersonalDataProcessing());
        newUser.setPersonalDataPublishing(registerUser.getPersonalDataPublishing());

        if (generateUserPasswordOnRegistration) {
            LOGGER.info("GENERATING RANDOM PASSWORD FOR USER: " + registerUser.getEmail());
            final String randomPassword = generateRandomUserPassword();
            newUser.setPassword(encryptAndSaltUserPassword(randomPassword));

            //SEND EMAIL WITH PASSWORD TO USER
            Map<String, String> templateVariables = new HashMap<>();
            templateVariables.put("userEmail", newUser.getEmail());
            templateVariables.put("userLoginPassword", randomPassword);
            emailServiceUtils.sendEmail(newUser.getEmail(), MAIL_APPLICATION_SUBJECT_NAME + MAIL_SUBJECT_REGISTER_NEW_USER, templateVariables, MAIL_TEMPLATE_REGISTER_NEW_USER_PASSWORD);

            userRepository.save(newUser);

        } else {
            LOGGER.info("USING PROVIDED PASSWORD FOR USER: " + registerUser.getEmail());
            newUser.setPassword(encryptAndSaltUserPassword(registerUser.getPassword()));
            // TODO Send confirmation email to user
            userRepository.save(newUser);
        }


        if(reguireRegisteredUserEmailConfirmation == true) {
            newUser.setIsAccountNonLocked(false);
            Map<String, String> templateVariables = new HashMap<>();
            templateVariables.put("activationLink", MAIL_APPLICATION_ACTIVATE_USER_URL + newUser.getUUID());
            LOGGER.info("SENDING EMAIL TO USER FOR ACCOUNT ACTIVATION: " + registerUser.getEmail());
            emailServiceUtils.sendEmail(newUser.getEmail(), MAIL_APPLICATION_SUBJECT_NAME + MAIL_SUBJECT_REGISTER_NEW_USER_CONFIRMATION_REQUIRED, templateVariables, MAIL_REGISTER_NEW_USER_CONFIRMATION);
        } else {
            newUser.setIsAccountNonLocked(false);
            // TODO Send Mail to user with actiovation details - account was created
            String encodedUserUUID = Base64.getUrlEncoder().encodeToString(newUser.getUUID().toString().getBytes());

            //SEND EMAIL TO USER WIT ACTIVATION LINK
            Map<String, String> templateVariables = new HashMap<>();
            templateVariables.put("userActivateLink", MAIL_APPLICATION_ACTIVATE_USER_URL + encodedUserUUID);
            LOGGER.info("SENDING EMAIL TO USER FOR ACCOUNT ACTIVATION: " + registerUser.getEmail());
            emailServiceUtils.sendEmail(newUser.getEmail(), MAIL_APPLICATION_SUBJECT_NAME + MAIL_SUBJECT_REGISTER_NEW_USER, templateVariables, MAIL_REGISTER_NEW_USER_CONFIRMATION);
        }



        if(reguireRegisteredUserAdminApproval == true) {
            //Set account as locked until it is approved by the admin
            newUser.setIsAccountNonLocked(false);
            String encodedUserUUID = Base64.getUrlEncoder().encodeToString(newUser.getUUID().toString().getBytes());

            //Send email to admin with activation link for user account
            Map<String, String> templateVariables = new HashMap<>();
            templateVariables.put("userActivateLink", MAIL_APPLICATION_ACTIVATE_USER_URL + encodedUserUUID);
            LOGGER.info("SENDING EMAIL TO ADMIN FOR USER APPROVAL: " + registerUser.getEmail());
            emailServiceUtils.sendEmail(MAIL_APPLICATION_ROOT_EMAIL, MAIL_APPLICATION_SUBJECT_NAME + MAIL_SUBJECT_REGISTER_NEW_USER, templateVariables, MAIL_REGISTER_NEW_USER_ADMIN_CONFIRMATION);

        } else {
            newUser.setIsAccountNonLocked(false);
            // TODO Send Mail to user with actiovation details - account was created
            String encodedUserUUID = Base64.getUrlEncoder().encodeToString(newUser.getUUID().toString().getBytes());

            //SEND EMAIL TO USER WIT ACTIVATION LINK
            Map<String, String> templateVariables = new HashMap<>();
            templateVariables.put("userActivateLink", MAIL_APPLICATION_ACTIVATE_USER_URL + encodedUserUUID);
            LOGGER.info("SENDING EMAIL TO USER FOR ACCOUNT ACTIVATION: " + registerUser.getEmail());
            emailServiceUtils.sendEmail(newUser.getEmail(), MAIL_APPLICATION_SUBJECT_NAME + MAIL_SUBJECT_REGISTER_NEW_USER, templateVariables, MAIL_REGISTER_NEW_USER_CONFIRMATION);
        }

        userRepository.save(newUser);

        // Build success response
        HttpResponse response = HttpResponse.builder()
                .httpTimestamp(new Date())
                .httpStatusCode(HttpStatus.CREATED.value())
                .httpStatus(HttpStatus.CREATED)
                .httpStatusReason(HttpStatus.CREATED.getReasonPhrase())
                .httpStatusMessage("User created successfully")
                .httpDeveloperMessage("The user with email " + registerUser.getEmail() + " has been created.")
                .httpPath(request.getRequestURI())
                .httpResponseData(Map.of("user", newUser)) // Include user data or other relevant data
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
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
        return jwtToken;
    }

    @Override
    public void requestUserPasswordReset(String email) throws IOException, MessagingException {

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.HOUR_OF_DAY, 24);
        Date expirationDate = calendar.getTime();

        User user = userRepository.findByEmail(email).orElseThrow();
        user.setPasswordResetOperationUUID(UUID.randomUUID());
        user.setPasswordResetExpiryDate(expirationDate);
        userRepository.save(user);

        String operationId = user.getPasswordResetOperationUUID().toString();
        String encodedOperationId = Base64.getUrlEncoder().encodeToString(operationId.getBytes());
        String userId = user.getUUID().toString();
        String encodedUserId = Base64.getUrlEncoder().encodeToString(userId.getBytes());

        //TODO change to debug for PROD
        LOGGER.info("Operation UUID = " + user.getPasswordResetOperationUUID() + " BASE64 String encodedOperationId = " + encodedOperationId);
        LOGGER.info("User UUID = " + user.getUUID() + " BASE64 String encodedUserId = " + encodedUserId);

        String passwordResetLinkString = MAIL_APPLICATION_BASE_URL + "?operationId=" + encodedOperationId + "&userId=" + encodedUserId;

        Map<String, String> templateVariables = new HashMap<>();
        templateVariables.put("userEmail", user.getEmail());
        templateVariables.put("userPasswordResetLink", passwordResetLinkString);
        emailServiceUtils.sendEmail(user.getEmail(), MAIL_APPLICATION_SUBJECT_NAME + MAIL_SUBJECT_PASSWORD_RESET_REQUEST, templateVariables, MAIL_TEMPLATE_RESET_USER_PASSWORD_REQUEST);

        userRepository.save(user);
    }

    public HttpResponse changeUserPassword(User passwordResetUser) throws MessagingException, IOException {
        User user = userRepository.findByEmail(passwordResetUser.getEmail()).orElseThrow();
        user.setPassword(encryptAndSaltUserPassword(passwordResetUser.getPassword()));
        userRepository.save(user);

        return null;
    }

    @Override
    public HttpResponse activateNewUser(UUID decodedUserUUID) throws MessagingException, IOException {
        //TODO implement user activation - this action should be only accessible by the admin
        return null;
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

            Path userFolder = Paths.get(USER_FOLDER + user.getUUID()).toAbsolutePath().normalize();
            Files.createDirectories(userFolder);
        }

    }

    /* PRIVATE METHODS */
    private Boolean userExistsByEmail(String userEmail) {
        Optional<User> user = userRepository.findByEmail((userEmail));
        return userRepository.findByEmail(userEmail).isPresent();
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

}
