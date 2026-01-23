package com.mentors.applicationstarter.Service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface VideoStreamingService {

    ResponseEntity<Resource> prepareContent(String lessonUuid, String range);
}
