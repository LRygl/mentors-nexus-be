package com.mentors.applicationstarter.Controller;

import com.mentors.applicationstarter.Service.VideoStreamingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/video")
@RequiredArgsConstructor
@Slf4j
public class VideoStreamingController {

    private final VideoStreamingService videoStreamingService;

    @GetMapping("/{lessonUuid}")
    public ResponseEntity<Resource> streamVideo(
            @PathVariable String lessonUuid,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range
    ) {
        log.info("Video stream request - UUID: {}, Range: {}", lessonUuid, range);
        return videoStreamingService.prepareContent(lessonUuid, range);
    }
}
