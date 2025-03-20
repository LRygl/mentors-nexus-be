package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.Model.Event;
import com.mentors.applicationstarter.Service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<Event>> listAllEvents() {
        List<Event> userList = eventService.getAllEvents();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }
}
