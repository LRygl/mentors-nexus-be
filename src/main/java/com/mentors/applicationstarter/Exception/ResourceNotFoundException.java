package com.mentors.applicationstarter.Exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends Exception {

    private final String errorMessage;
    private final String developerMessage;

    public ResourceNotFoundException(String errorMessage, String developerMessage) {
        super("Resource not found");
        this.errorMessage = errorMessage;
        this.developerMessage = developerMessage;
    }

}
