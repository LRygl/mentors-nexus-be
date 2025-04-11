package com.mentors.applicationstarter.Service.Impl;

import com.mentors.applicationstarter.Enum.EventCategory;
import com.mentors.applicationstarter.Enum.EventType;
import com.mentors.applicationstarter.Model.Event;
import com.mentors.applicationstarter.Repository.EventRepository;
import com.mentors.applicationstarter.Service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    @Async
    public void generateEvent(UUID resourceUUID, String eventName,String value, EventCategory eventCategory, EventType eventType, String origin) {
        Event event = Event.builder()
                .UUID(UUID.randomUUID())
                .resourceUUID(resourceUUID)
                .name(eventName)
                .value(value)
                .category(eventCategory)
                .type(eventType)
                .origin(origin)
                .timestamp(Instant.now()
                )
                .build();

        eventRepository.save(event);
    }

    public List<Event> getAllEvents(){
        return eventRepository.findAll();
    }

    @Override
    public Optional<List<Event>> getEventList(UUID resourceUUID) {
        return eventRepository.findByResourceUUID(resourceUUID);
    }

    @Override
    public List<Event> findByResourceUUIDAndEventType(UUID uuid, EventType type) {
        return eventRepository.findByResourceUUIDAndType(uuid,type);
    }
}
