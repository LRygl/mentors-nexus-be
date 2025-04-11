package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.mentors.applicationstarter.Enum.EventType;

public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<List<Event>> findByResourceUUID(UUID resourceUUID);


    List<Event> findByIdAndEventType(Long id, EventType type);
}
