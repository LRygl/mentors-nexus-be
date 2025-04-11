package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Enum.ConsentType;
import com.mentors.applicationstarter.Enum.EventCategory;
import com.mentors.applicationstarter.Enum.EventType;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.Event;
import com.mentors.applicationstarter.Model.Request.UserConsentUpdateRequest;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Repository.UserRepository;
import com.mentors.applicationstarter.Service.EventService;
import com.mentors.applicationstarter.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final EventService eventService;

    @Override
    public List<User> getUserList() {
        List<User> userList = userRepository.findAll().stream().toList();
        LOGGER.info("Returning list of application users: {}", userList.size());
        return userList;
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

}
