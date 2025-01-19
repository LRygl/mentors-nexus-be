package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    List<User> getUserList();

    User getUserById(Long userId) throws ResourceNotFoundException;
}
