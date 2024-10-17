package com.mentors.applicationstarter.Model.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-DD-dd hh:mm:ss", timezone = "Europe/Prague")
    private Date httpTimestamp;
    private int httpStatusCode;
    private HttpStatus httpStatus;
    private String httpStatusReason;
    private String httpStatusMessage;
    private String httpDeveloperMessage;
    private String httpPath;
    private Map<?, ?> httpResponseData;

}
