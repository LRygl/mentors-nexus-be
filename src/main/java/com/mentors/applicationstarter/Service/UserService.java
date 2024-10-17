package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    User getUserById(Long userId) throws ResourceNotFoundException;

}
