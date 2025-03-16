package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface UserService {
    List<User> getUserList();

    Optional<User> getUserByEmail(String userEmail);
    Optional<User> getUserByUUID(UUID userUUID);
    Optional<User> getUserById(Long userId) throws ResourceNotFoundException;
}
