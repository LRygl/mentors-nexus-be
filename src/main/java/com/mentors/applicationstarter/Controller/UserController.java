package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.DTO.UserResponseDTO;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Model.Course;
import com.mentors.applicationstarter.Model.Event;
import com.mentors.applicationstarter.Model.Request.UserConsentUpdateRequest;
import com.mentors.applicationstarter.Model.User;
import com.mentors.applicationstarter.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listAllUsers() {
        return new ResponseEntity<>(userService.getUserList(), HttpStatus.OK);
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

    @GetMapping("/{id}/course")
    public ResponseEntity<Course> getUserCourses(@PathVariable Long userId){
        return new ResponseEntity<>(userService.getUserCourses(userId),HttpStatus.OK);
    }


    //TODO Consent Management Endpoint
    @PutMapping("/{id}/consent")
    public ResponseEntity<?> updateUserConsents(
            @PathVariable Long id,
            @RequestBody UserConsentUpdateRequest request,
            HttpServletRequest httpRequest
    ) {
        userService.updateConsents(id,request, httpRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/consent/history")
    public ResponseEntity<List<Event>>getConsentEvents() {
        return new ResponseEntity<>(userService.getConsentEvents(),HttpStatus.OK);
    }
    @GetMapping("/{id}/consent/history")
    public ResponseEntity<List<Event>>getUserConsentEvents(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUserConsentEvents(id),HttpStatus.OK);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<User> changeUserRole(@PathVariable Long id, @RequestBody User request) {
        return new ResponseEntity<>(userService.changeUserRole(id, request.getRoleName()), HttpStatus.OK);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<User> activateUser(@PathVariable Long id) throws ResourceNotFoundException {
        return new ResponseEntity<>(userService.activateUser(id),HttpStatus.OK);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<User> deactivateUser(@PathVariable Long id) throws ResourceNotFoundException {
        return new ResponseEntity<>(userService.deactivateUser(id),HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) throws ResourceNotFoundException {
        return new ResponseEntity<>(userService.deleteUser(id),HttpStatus.GONE);
    }

}
