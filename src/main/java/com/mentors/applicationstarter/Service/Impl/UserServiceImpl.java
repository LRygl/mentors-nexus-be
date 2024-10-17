package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public User getUserById(Long userId) throws ResourceNotFoundException {
        return null;
    }



}
