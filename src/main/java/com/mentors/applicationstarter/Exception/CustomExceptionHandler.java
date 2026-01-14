package com.mentors.applicationstarter.Exception;

import com.mentors.applicationstarter.Model.Response.HttpErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class CustomExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomExceptionHandler.class);
    private ResponseEntity<HttpErrorResponse> createErrorHttpResponse(HttpStatus httpStatus, String applicationErrorCode, String applicationErrorMessage) {
        // Construct your response body using the error message and custom messages
        return ResponseEntity
                .status(HttpStatus.resolve(httpStatus.value()))
                .body(HttpErrorResponse.builder()
                        .httpTimestamp(new Date(System.currentTimeMillis()))
                        .httpStatus(httpStatus)
                        .httpStatusCode(httpStatus.value())
                        .applicationErrorCode(applicationErrorCode)
                        .applicationErrorMessage(applicationErrorMessage)
                        .build()
                );
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<HttpErrorResponse> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        LOGGER.error(ex.getDeveloperMessage());
        return createErrorHttpResponse(HttpStatus.CONFLICT, ex.getErrorCode().getCode(), ex.getMessage());
    }

    @ExceptionHandler(ResourceImmutableException.class)
    public ResponseEntity<HttpErrorResponse> handleResourceImmutableException(ResourceImmutableException ex) {
        LOGGER.error(ex.getDeveloperMessage());
        return createErrorHttpResponse(HttpStatus.CONFLICT, ex.getErrorCode().getCode(), ex.getMessage());
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<HttpErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        LOGGER.error(ex.getDeveloperMessage());
        return createErrorHttpResponse(HttpStatus.NOT_FOUND, ex.getErrorCode().getCode(), ex.getMessage());
    }

    @ExceptionHandler(ResourceNotEmptyException.class)
    public ResponseEntity<HttpErrorResponse> handleResourceNotemptyException(ResourceNotEmptyException ex) {
        LOGGER.error(ex.getDeveloperMessage());
        return createErrorHttpResponse(HttpStatus.NOT_FOUND, ex.getErrorCode().getCode(), ex.getMessage());
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<HttpErrorResponse> handleBusinessRuleViolationException(BusinessRuleViolationException ex) {
        LOGGER.error(ex.getDeveloperMessage());
        return createErrorHttpResponse(HttpStatus.CONFLICT, ex.getErrorCode().getCode(), ex.getMessage());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<HttpErrorResponse> handleInvalidRequestException(InvalidRequestException ex) {
        LOGGER.error(ex.getDeveloperMessage());
        return createErrorHttpResponse(HttpStatus.BAD_REQUEST, ex.getErrorCode().getCode(), ex.getMessage());
    }

    @ExceptionHandler(ConfigurationException.class)
    public ResponseEntity<HttpErrorResponse> handleConfigurationException(ConfigurationException ex) {
        LOGGER.error(ex.getDeveloperMessage());
        return createErrorHttpResponse(HttpStatus.BAD_REQUEST, ex.getErrorCode().getCode(), ex.getMessage());
    }

}
