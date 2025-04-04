package com.mentors.applicationstarter.Utils;

import com.mentors.applicationstarter.Model.Response.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HttpResponseFactory {

    public static HttpResponse ok(String message, String developerMessage, Map<?, ?> data) {
        return HttpResponse.builder()
                .httpTimestamp(new Date())
                .httpStatusCode(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .httpStatusReason(HttpStatus.OK.getReasonPhrase())
                .httpStatusMessage(message)
                .httpDeveloperMessage(developerMessage)
                .httpResponseData(data)
                .build();
    }

    public static HttpResponse notFound(String message, String developerMessage) {
        return HttpResponse.builder()
                .httpTimestamp(new Date())
                .httpStatusCode(HttpStatus.NOT_FOUND.value())
                .httpStatus(HttpStatus.NOT_FOUND)
                .httpStatusReason(HttpStatus.NOT_FOUND.getReasonPhrase())
                .httpStatusMessage(message)
                .httpDeveloperMessage(developerMessage)
                .build();
    }

}
