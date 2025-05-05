package com.mentors.applicationstarter.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ErrorCodes {
    REGISTRATION_NOT_ALLOWED("UserRegistrationNotAllowed", "User registration is not allowed","Application is configured to not allow user registration"),
    USER_ALREADY_REGISTERED("UserAlreadyRegistered", "User with email: %s already exists", "User already exists with email: %s"),
    USER_DOES_NOT_EXIST("UserDoesNotExist", "User does not exists", "User does not exist"),

    CATEGORY_EMPTY("CategoryCannotBeEmpty", "Category name cannot be empty", "Request to create category was received but the 'name' parameter is empty after trimming the data."),
    CATEGORY_EXISTS("CategoryAlreadyExists", "Category already exists", "Category with this name already exists and cannot be created again"),


    COURSE_DOES_NOT_EXIST("CourseDoesNotExist","Course was not found", "Could not find course based on the request data provided");
    @Getter
    private final String code;
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
