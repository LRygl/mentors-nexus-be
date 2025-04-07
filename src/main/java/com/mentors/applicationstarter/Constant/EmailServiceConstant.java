package com.mentors.applicationstarter.Constant;

import org.springframework.beans.factory.annotation.Value;

import static com.mentors.applicationstarter.Constant.ApplicationConstant.APP_URL;

public class EmailServiceConstant {

    public static final String MAIL_APPLICATION_BASE_URL = APP_URL + "/auth/confirm-password-reset";
    public static final String MAIL_APPLICATION_ACTIVATE_USER_URL = APP_URL + "/auth/activate?userUUID=";
    public static final String MAIL_APPLICATION_SUBJECT_NAME = "App.cz - ";
    public static final String MAIL_APPLICATION_ROOT_EMAIL = "lubomir.rygl@gmail.com";



    //REGISTER NEW USER MAIL
    public static final String MAIL_SUBJECT_REGISTER_NEW_USER = "Registrace nového uživatele";
    public static final String MAIL_SUBJECT_REGISTER_NEW_USER_CONFIRMATION_REQUIRED = "Potvrzení nového uživatelského účtu";
    public static final String MAIL_SUBJECT_PASSWORD_RESET_REQUEST = "Žádost o reset uživatelského hesla";
    public static final String MAIL_SUBJECT_PASSWORD_RESET_NEW_PASSWORD = "Nové uživatelské heslo";



    //MAIL TEMPLATES
    public static final String MAIL_TEMPLATE_REGISTER_NEW_USER_PASSWORD = "mail-register-new-user-generated-password";
    public static final String MAIL_TEMPLATE_REGISTER_PROVIDED_USER_PASSWORD_ACTIVATION = "mail-register-provided-user-password-activation";
    public static final String MAIL_TEMPLATE_REGISTER_PROVIDED_USER_PASSWORD = "mail-register-provided-user-password";


    public static final String MAIL_TEMPLATE_RESET_USER_PASSWORD_REQUEST = "mail-reset-password-request";
    public static final String MAIL_TEMPLATE_PASSWORD_RESET_NEW_PASSWORD = "mail-reset-password-new-password";

}
