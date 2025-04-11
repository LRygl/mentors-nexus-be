package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.Enum.EventCategory;
import com.mentors.applicationstarter.Enum.EventType;
import com.mentors.applicationstarter.Model.Event;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface EventService {

    void generateEvent(UUID resourceUUID, String eventName, String value, EventCategory eventCategory, EventType eventType, String origin);
    List<Event> getAllEvents();
    Optional<List<Event>> getEventList(UUID resourceUUID);


    List<Event> getEventListByUserID(Long id, EventType type);
}
