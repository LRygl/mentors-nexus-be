package com.mentors.applicationstarter.Service;

import com.mentors.applicationstarter.Model.Event;
import org.springframework.stereotype.Service;

@Service
public interface EventService {

    void storeEvent(Event event);

}
