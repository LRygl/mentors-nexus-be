package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.DTO.CourseResponseDTO;
import com.mentors.applicationstarter.DTO.CourseSummaryDTO;
import com.mentors.applicationstarter.DTO.UserResponseDTO;
import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Enum.EventCategory;
import com.mentors.applicationstarter.Enum.EventType;
import com.mentors.applicationstarter.Enum.Role;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.Event;
import com.mentors.applicationstarter.Model.Request.UserConsentUpdateRequest;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Repository.UserRepository;
import com.mentors.applicationstarter.Service.EventService;
import com.mentors.applicationstarter.Service.UserService;
import com.mentors.applicationstarter.Utils.UserColorGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final EventService eventService;

    @Override
    public List<UserResponseDTO> getUserList() {
        //List<User> userList = userRepository.findAll().stream().toList();
        //LOGGER.info("Returning list of application users: {}", userList.size());

        return userRepository.findAll().stream()
                .map(this::mapUserToDTO)
                .collect(Collectors.toList());

        //return userList;
    }

    //TODO Paginated user list


    //TODO Paginated user list with filter

    @Override
    public Optional<User> getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail);
    }

    @Override
    public Optional<User> getUserByUUID(UUID userUUID) {
        return userRepository.findByUUID(userUUID);
    }

    @Override
    public Optional<User> getUserById(Long userId) throws ResourceNotFoundException {
        return userRepository.findById(userId);
    }

    public void updateConsents(Long userId, UserConsentUpdateRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        boolean changed = false;
        //String ip = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        if (request.getMarketingConsent() != null &&
                !Objects.equals(user.getMarketing(), request.getMarketingConsent())) {

            user.setMarketing(request.getMarketingConsent());
            eventService.generateEvent(user.getUUID(),"Marketing Consent", request.getMarketingConsent().toString() , EventCategory.USER, EventType.CONSENT_UPDATE ,this.getClass().getSimpleName());
            changed = true;
        }

        if (request.getCookiePolicyConsent() != null &&
                !Objects.equals(user.getCookiePolicyConsent(), request.getCookiePolicyConsent())) {

            user.setCookiePolicyConsent(request.getCookiePolicyConsent());
            eventService.generateEvent(user.getUUID(),"Cookie Consent", request.getCookiePolicyConsent().toString() , EventCategory.USER, EventType.CONSENT_UPDATE ,this.getClass().getSimpleName());

            changed = true;
        }

        if (request.getPersonalDataProcessingConsent() != null &&
                !Objects.equals(user.getPersonalDataProcessing(), request.getPersonalDataProcessingConsent())) {

            user.setPersonalDataProcessing(request.getPersonalDataProcessingConsent());
            eventService.generateEvent(user.getUUID(),"Personal Data Processing Consent", request.getPersonalDataProcessingConsent().toString() , EventCategory.USER, EventType.CONSENT_UPDATE ,this.getClass().getSimpleName());
            changed = true;
        }

        if (request.getPersonalDataPublishingConsent() != null &&
                !Objects.equals(user.getPersonalDataPublishing(), request.getPersonalDataPublishingConsent())) {

            user.setPersonalDataPublishing(request.getPersonalDataPublishingConsent());
            eventService.generateEvent(user.getUUID(),"Personal Data Publishing Consent", request.getPersonalDataPublishingConsent().toString() , EventCategory.USER, EventType.CONSENT_UPDATE ,this.getClass().getSimpleName());
            changed = true;
        }

        if (changed) {
            userRepository.save(user);
        }
    }

    @Override
    public List<Event> getUserConsentEvents(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return eventService.findByResourceUUIDAndEventType(user.getUUID(), EventType.CONSENT_UPDATE);
    }

    @Override
    public List<Event> getConsentEvents() {
        return eventService.findAllEventsByType(EventType.CONSENT_UPDATE);
    }

    @Override
    public User activateUser(Long id) throws ResourceNotFoundException {
        User user = findUser(id);
        activate(user);
        return user;
    }

    @Override
    public User deactivateUser(Long id) throws ResourceNotFoundException {
        User user = findUser(id);
        deactivate(user);
        return user;
    }

    @Override
    public User deleteUser(Long id) throws ResourceNotFoundException {
        User user = findUser(id);
        if (user.getId() != 1L){
            userRepository.deleteById(id);
            return user;
        }
        throw new RuntimeException("Cannot delete user");
    }

    @Override
    public User changeUserRole(Long id, String roleString) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.USER_DOES_NOT_EXIST));

        Role role;
        try {
            role = Role.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role");
        }
        user.setRole(role);
        return userRepository.save(user);
    }

    @Override
    public Course getUserCourses(Long userId) {
        User user = findUser(userId);
        return null;
    }

    @Override
    public UserResponseDTO getUserByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(ErrorCodes.USER_DOES_NOT_EXIST));
        return mapUserToDTO(user);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// PRIVATE METHODS ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void activate(User user) {
        user.setIsAccountNonLocked(true);
        userRepository.save(user);
    }

    private void deactivate(User user) {
        user.setIsAccountNonLocked(false);
        userRepository.save(user);
    }

    private <T> User findUser(T identifier) throws ResourceNotFoundException {
        return switch (identifier) {
            case Long l -> userRepository.findById(l).orElseThrow(()-> new ResourceNotFoundException(ErrorCodes.USER_DOES_NOT_EXIST));
            case UUID uuid -> userRepository.findByUUID(uuid).orElseThrow(()-> new ResourceNotFoundException(ErrorCodes.USER_DOES_NOT_EXIST));
            case String s -> userRepository.findByEmail(s).orElseThrow(()-> new ResourceNotFoundException(ErrorCodes.USER_DOES_NOT_EXIST));
            case null, default -> throw new RuntimeException("Invalid identifier");
        };
    }

    private UserResponseDTO mapUserToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .UUID(user.getUUID())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .telephoneNumber(user.getTelephoneNumber())
                .lastLoginDateDisplay(user.getLastLoginDateDisplay())
                .registerDate(user.getRegisterDate())
                .lastUpdatedDate(user.getLastUpdatedDate())
                .isAccountNonLocked(user.getIsAccountNonLocked())
                .forcePasswordChangeOnLogin(user.getForcePasswordChangeOnLogin())
                .personalDataProcessing(user.getPersonalDataProcessing())
                .personalDataPublishing(user.getPersonalDataPublishing())
                .marketing(user.getMarketing())
                .cookiePolicyConsent(user.getCookiePolicyConsent())
                .role(user.getRoleName())
                .lightBg(user.getLightBg())
                .darkBg(user.getDarkBg())
                .ownedCourses(user.getOwnedCourses().stream()
                        .map(course -> CourseSummaryDTO.builder()
                                .id(course.getId())
                                .name(course.getName())
                                .status(course.getStatus().name())
                                .uuid(course.getUuid())
                                .build())
                        .collect(Collectors.toSet()))
                .joinedCourses(user.getJoinedCourses().stream()
                        .map(course -> CourseSummaryDTO.builder()
                                .id(course.getId())
                                .name(course.getName())
                                .uuid(course.getUuid())
                                .status(course.getStatus().name())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }


}
