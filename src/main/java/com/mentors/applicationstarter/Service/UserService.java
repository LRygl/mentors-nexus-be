package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.DTO.UserResponseDTO;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.Event;
import com.mentors.applicationstarter.Model.Request.UserConsentUpdateRequest;
import com.mentors.applicationstarter.Model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface UserService {
    List<UserResponseDTO> getUserList();

    Optional<User> getUserByEmail(String userEmail);
    Optional<User> getUserByUUID(UUID userUUID);
    Optional<User> getUserById(Long userId) throws ResourceNotFoundException;

    void updateConsents(Long id, UserConsentUpdateRequest request, HttpServletRequest httpRequest);

    List<Event> getUserConsentEvents(Long id);
    List<Event> getConsentEvents();

    User activateUser(Long id) throws ResourceNotFoundException;
    User deactivateUser(Long id) throws ResourceNotFoundException;

    User deleteUser(Long id) throws ResourceNotFoundException;

    User changeUserRole(Long id, String role);

    Course getUserCourses(Long userId);

    UserResponseDTO getUserByUserId(Long userId);
}
