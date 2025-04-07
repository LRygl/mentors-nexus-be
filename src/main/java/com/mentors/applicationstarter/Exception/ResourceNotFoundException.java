package com.mentors.applicationstarter.Exception;

import com.mentors.applicationstarter.Enum.ErrorCodes;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends Exception {

    private final ErrorCodes errorCode;
    private final String developerMessage;

    public ResourceNotFoundException(ErrorCodes errorCode) {
        super(errorCode.getCustomerMessage());
        this.errorCode = errorCode;
        this.developerMessage = errorCode.getDeveloperMessage();
    }

}
