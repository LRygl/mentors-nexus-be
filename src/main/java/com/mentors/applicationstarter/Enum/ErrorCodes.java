package com.mentors.applicationstarter.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ErrorCodes {
    REGISTRATION_NOT_ALLOWED(550, "User registration is not allowed","Application is configured to not allow user registration"),
    USER_ALREADY_REGISTERED(551, "User with email: %s already exists", "User already exists with email: %s"),
    USER_DOES_NOT_EXIST(552, "User does not exists", "User does not exist");




    @Getter
    private final int code;
    @Getter
    private final String customerMessage;
    @Getter
    private final String developerMessage;

    // Utility method to format the customer message with dynamic parameters
    public String formatCustomerMessage(Object... args) {
        return String.format(customerMessage, args);
    }

    // Utility method to format the developer message with dynamic parameters
    public String formatDeveloperMessage(Object... args) {
        return String.format(developerMessage, args);
    }


}
