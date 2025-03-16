package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Repository.UserRepository;
import com.mentors.applicationstarter.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

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

}
