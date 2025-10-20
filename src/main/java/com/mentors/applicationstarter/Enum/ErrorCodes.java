package com.mentors.applicationstarter.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ErrorCodes {
    REGISTRATION_NOT_ALLOWED("UserRegistrationNotAllowed", "User registration is not allowed","Application is configured to not allow user registration"),
    USER_ALREADY_REGISTERED("UserAlreadyRegistered", "User with email: %s already exists", "User already exists with email: %s"),
    USER_DOES_NOT_EXIST("UserDoesNotExist", "User does not exists", "User does not exist"),

    CATEGORY_DOES_NOT_EXIST("CategoryDoesNotExist","Category does not exist","Category does not exist"),
    CATEGORY_EMPTY("CategoryCannotBeEmpty", "Category name cannot be empty", "Request to create category was received but the 'name' parameter is empty after trimming the data."),
    CATEGORY_EXISTS("CategoryAlreadyExists", "Category already exists", "Category with this name already exists and cannot be created again"),


    COURSE_DOES_NOT_EXIST("CourseDoesNotExist","Course was not found", "Could not find course based on the request data provided"),
    COURSE_CANNOT_BE_ASSIGNED("CourseCannotBeAssigned", "User cannot be course owner", "Ordinary users cannot be owning a course. Elevate user Role to assign him to course."),

    COURSE_SECTION_DOES_NOT_EXIST("CourseSectionDoesNotExist","Course section does not exist","Could not find the course section with the Course Section ID Provided in the request"),

    LESSON_NOT_FOUND("LessonDoesNotExist","Lesson does not exist", "Lesson does not exist by lesson id"),

    INVALID_TOKEN("AUTH_INVALID_TOKEN", "Token is not valid","Token is not valid"),
    TOKEN_NOT_FOUND("AUTH_TOKEN_NOT_FOUND", "Token was not find", "Refresh the token"),


    DISCOUNT_COURSE_DOES_NOT_EXIST("CourseDiscountDoesNotExist","",""),
    DISCOUNT_VALID_FROM_IN_PAST("CourseDiscountValidFromInPast","",""),
    DISCOUNT_VALID_FROM_AFTER_VALID_TO("","",""),
    DISCOUNT_VALID_TO_IN_PAST("","",""),
    DISCOUNT_ALREADY_STARTED("","",""),
    DISCOUNT_TYPE_AMBIGUOUS("","",""),
    DISCOUNT_CONCURRENT_DISCOUNT_NOT_ALOWED("CourseDiscountConcurrentNotAlowed","It is not alowed to have multiple overlapping discounts for the same course","Shits on fire yo!"),
    DISCOUNT_VALUE_LARGER_THAN_PRICE("CourseDiscountAmountLargerThanPrice","The discount amount cannot be larger than the price of the course.",""),
    DISCOUNT_PERCENTAGE_TOO_LOW("DiscountPercentageTooLowOrNegative","",""),
    DISCOUNT_PERCENTAGE_TOO_HIGH("DiscountPercentageTooHigh","",""),
    DISCOUNT_PERCENTAGE_AND_AMOUNT_PROVIDED("DiscountPercentageAndAmountProvided","Only Percentage discount or Amount can be provided in the rquest", ""),

    COMPANY_DOES_NOT_EXIST("","",""),
    COMPANY_ALLREADY_EXISTS_BY_VAT("CompanyDuplicateVATRecord","VAT value for the company is allready used",""),
    COMPANY_REQUEST_VAT_REQUIRED("CompanyRequestVATMandatory","VAT is a mandatory value for company registration",""),
    COMPANY_USER_ALLREADY_ENROLLED("UserAllreadyEnroled", "This user was allready enrolled to this company as an employee",""),
    INVOICE_DOES_NOT_EXIST("InvoiceDoesNotExist","Invoice lookup failed - invoice with this identification does not exist",""),

    // FAQ Category error codes
    FAQ_CATEGORY_NOT_FOUND("FAQCategoryNotFound", "FAQ Category not found", "FAQ Category with the specified identifier was not found"),
    FAQ_CATEGORY_NAME_EXISTS("FAQCategoryNameExists", "FAQ Category name '%s' already exists", "A FAQ category with name '%s' already exists in the system"),
    FAQ_CATEGORY_SLUG_EXISTS("FAQCategorySlugExists", "FAQ Category slug '%s' already exists", "A FAQ category with slug '%s' already exists in the system"),
    FAQ_CATEGORY_HAS_FAQS("FAQCategoryHasFAQs", "Cannot delete category with existing FAQs", "FAQ Category cannot be deleted because it contains FAQs. Move or delete FAQs first"),
    FAQ_CATEGORY_INVALID_NAME("FAQCategoryInvalidName", "Invalid category name", "FAQ Category name cannot be empty and must be less than 100 characters"),
    FAQ_CATEGORY_INVALID_COLOR("FAQCategoryInvalidColor", "Invalid color code format", "Color code must be in hex format like #FF0000"),
    FAQ_CATEGORY_INACTIVE("FAQCategoryInactive", "Category is not active", "Cannot perform operation on inactive FAQ category"),

    // FAQ error codes
    FAQ_NOT_FOUND("FAQNotFound", "FAQ not found", "FAQ with the specified identifier was not found"),
    FAQ_SLUG_EXISTS("FAQSlugExists", "FAQ slug '%s' already exists in this category", "A FAQ with slug '%s' already exists in the specified category"),
    FAQ_INVALID_QUESTION("FAQInvalidQuestion", "Invalid FAQ question", "FAQ question cannot be empty and must be less than 500 characters"),
    FAQ_INVALID_ANSWER("FAQInvalidAnswer", "Invalid FAQ answer", "FAQ answer cannot be empty"),
    FAQ_CATEGORY_REQUIRED("FAQCategoryRequired", "FAQ category is required", "Every FAQ must be assigned to a category"),
    FAQ_ALREADY_PUBLISHED("FAQAlreadyPublished", "FAQ is already published", "The specified FAQ is already in published status"),
    FAQ_NOT_PUBLISHED("FAQNotPublished", "FAQ is not published", "The specified FAQ is not in published status"),
    FAQ_INVALID_STATUS_TRANSITION("FAQInvalidStatusTransition", "Invalid status transition", "Cannot transition FAQ from current status to requested status"),
    FAQ_DUPLICATE_QUESTION("FAQDuplicateQuestion", "FAQ question already exists in category", "A FAQ with similar question already exists in this category"),
    FAQ_VALIDATION_FAILED("FAQValidationFailed", "FAQ validation failed", "FAQ data validation failed - check required fields and constraints");


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
