package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> listAllUsers() {
        List<User> userList = userService.getUserList();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<Optional<User>> filterUser(
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) UUID userUUID,
            @RequestParam(required = false) Long id
    ) throws ResourceNotFoundException {
        if(userEmail!=null){
            Optional<User> filteredUser = userService.getUserByEmail(userEmail);
            return new ResponseEntity<>(filteredUser,HttpStatus.OK);
        } else if (userUUID!=null) {
            Optional<User> filteredUser = userService.getUserByUUID(userUUID);
            return new ResponseEntity<>(filteredUser, HttpStatus.OK);
        } else if (userService.getUserById(id).isPresent()) {
            Optional<User> filteredUser = userService.getUserById(id);
            return new ResponseEntity<>(filteredUser, HttpStatus.OK);
        } else {
            return null;
        }
    }

}
