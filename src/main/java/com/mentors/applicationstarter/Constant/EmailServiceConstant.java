package com.mentors.applicationstarter.Constant;

import org.springframework.beans.factory.annotation.Value;

public class EmailServiceConstant {

    public static final String MAIL_APPLICATION_BASE_URL = "http://localhost:8080" + "/api/v1/auth/confirm-password-reset";
    public static final String MAIL_APPLICATION_ACTIVATE_USER_URL = "http://localhost:8080" + "/api/v1/auth/activate?userUUID=";
    public static final String MAIL_APPLICATION_SUBJECT_NAME = "MůjPoukaz.cz - ";
    public static final String MAIL_APPLICATION_ROOT_EMAIL = "lubomir.rygl@gmail.com";



    //REGISTER NEW USER MAIL
    public static final String MAIL_SUBJECT_REGISTER_NEW_USER = "Registrace nového uživatele";
    public static final String MAIL_SUBJECT_REGISTER_NEW_USER_CONFIRMATION_REQUIRED = "Potvrzení nového uživatelského účtu";
    public static final String MAIL_SUBJECT_PASSWORD_RESET_REQUEST = "Žádost o reset uživatelského hesla";



    //MAIL TEMPLATES
    public static final String MAIL_TEMPLATE_REGISTER_NEW_USER_PASSWORD = "mail-register-new-user-password";
    public static final String MAIL_TEMPLATE_RESET_USER_PASSWORD_REQUEST = "mail-reset-password-request";
    public static final String MAIL_REGISTER_NEW_USER_ADMIN_CONFIRMATION = "mail-register-new-user-admin-confirmation";
    public static final String MAIL_REGISTER_NEW_USER_CONFIRMATION = "mail-register-new-user-confirmation";
}
