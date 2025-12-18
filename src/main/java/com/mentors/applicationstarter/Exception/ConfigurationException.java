package com.mentors.applicationstarter.Exception;

import com.mentors.applicationstarter.Enum.ErrorCodes;
import lombok.Getter;

@Getter
public class ConfigurationException extends RuntimeException {

    private final ErrorCodes errorCode;
    private final String developerMessage;

    public ConfigurationException(ErrorCodes errorCode) {
        super(errorCode.getCustomerMessage());
        this.errorCode = errorCode;
        this.developerMessage = errorCode.getDeveloperMessage();
    }
}
