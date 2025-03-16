package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
