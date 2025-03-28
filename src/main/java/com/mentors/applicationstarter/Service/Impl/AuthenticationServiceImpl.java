package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Enum.EventCategory;
import com.mentors.applicationstarter.Exception.ResourceAlreadyExistsException;
import com.mentors.applicationstarter.Model.Event;
import com.mentors.applicationstarter.Model.Response.HttpResponse;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Repository.UserRepository;
import com.mentors.applicationstarter.Service.AuthenticationService;
import com.mentors.applicationstarter.Service.EventService;
import com.mentors.applicationstarter.Service.JwtService;
import com.mentors.applicationstarter.Enum.Role;
import com.mentors.applicationstarter.Utils.Base64Utils;
import com.mentors.applicationstarter.Utils.EmailServiceUtils;
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
import java.time.Instant;
import java.util.*;

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


    //TODO
    // IF ADMIN ConfirmationRequired
        // IF 

    // ELSE Create directly


    @Override
    public ResponseEntity<HttpResponse> handleUserRegistrationRequest(User registeredUser, HttpServletRequest request) throws ResourceAlreadyExistsException {
        String passwordGenerationStrategy = "useProvidedPassword";
        boolean requireUserEmailConfirmation = true;

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

        if (saveUser(user)) {
            //Process registration based on the selected password generation strategy
            switch (passwordGenerationStrategy) {
                case "generateUserPasswordsStrategy":
                    String generatedSecurePassword = generateRandomUserPassword();
                    user.setPassword(encryptAndSaltUserPassword(generatedSecurePassword));

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
                        String userAccountActivationUrl = String.format("%s/%s%s", APP_URL,"/auth/activate?activationId=", base64EncodedActivationString);
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
                        emailServiceUtils.sendEmail(user.getEmail(), MAIL_APPLICATION_SUBJECT_NAME + MAIL_SUBJECT_REGISTER_NEW_USER, providedPasswordTemplateVariables, MAIL_TEMPLATE_REGISTER_NEW_USER_PASSWORD);
                    }
                    saveUser(user);
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + passwordGenerationStrategy);
            }
        } else {

            return null;
        }

        // Build success response
        HttpResponse response = HttpResponse.builder()
                .httpTimestamp(new Date())
                .httpStatusCode(HttpStatus.CREATED.value())
                .httpStatus(HttpStatus.CREATED)
                .httpStatusReason(HttpStatus.CREATED.getReasonPhrase())
                .httpStatusMessage("User created successfully")
                .httpDeveloperMessage("The user with email " + user.getEmail() + " has been created.")
                .httpPath(request.getRequestURI())
                .httpResponseData(Map.of("user", user)) // Include user data or other relevant data
                .build();

        eventService.generateEvent(user.getUUID(),EVENT_AUTH_USER_REGISTERED ,EventCategory.USER,this.getClass().getSimpleName());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Transactional
    private boolean saveUser(User user) {
        try {
            userRepository.save(user);
            LOGGER.info("USER SAVED TO DATABASE");
            return user.getId() != null;
        } catch (Exception e) {
            LOGGER.error("USER WAS NOT SAVED TO DATABASE!");
            return false;
        }
    }

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

        //User Password will be generated by the application during the registration process
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

        //User Password will be provided by the user during the registration process
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
        eventService.generateEvent(user.getUUID(),"User Authentication Request",EventCategory.USER,this.getClass().getSimpleName());

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
    public HttpResponse activateNewUser(String activationString) throws MessagingException, IOException {
        //TODO implement user activation - this action should be only accessible by the admin
        String[] securityString = activationString.split("\\+",2);
        String activationUUID = securityString[0];
        String activationTimestamp = securityString[1];
        eventService.generateEvent(UUID.fromString(activationUUID),"User Account Activated",EventCategory.USER,this.getClass().getSimpleName());

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
            eventService.generateEvent(user.getUUID(),"New User Registered",EventCategory.USER,this.getClass().getSimpleName());
            Path userFolder = Paths.get(USER_FOLDER + user.getUUID()).toAbsolutePath().normalize();
            Files.createDirectories(userFolder);
        }

    }

    /* PRIVATE METHODS */
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

}
