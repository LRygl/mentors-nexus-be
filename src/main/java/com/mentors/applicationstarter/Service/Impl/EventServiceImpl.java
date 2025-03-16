package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Model.Event;
import com.mentors.applicationstarter.Repository.EventRepository;
import com.mentors.applicationstarter.Service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public void storeEvent(Event eventData){
        eventRepository.save(eventData);
    }
}
