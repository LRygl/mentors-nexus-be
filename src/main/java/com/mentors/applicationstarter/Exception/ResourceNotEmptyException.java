package com.mentors.applicationstarter.Exception;

import com.mentors.applicationstarter.Enum.ErrorCodes;
import lombok.Getter;

@Getter
public class ResourceNotEmptyException extends RuntimeException {

    private final ErrorCodes errorCode;
    private final String developerMessage;

    public ResourceNotEmptyException(ErrorCodes errorCode, Object... messageArgs) {
        super(errorCode.formatCustomerMessage(messageArgs));
        this.errorCode = errorCode;
        this.developerMessage = errorCode.formatDeveloperMessage(messageArgs);
    }
}
