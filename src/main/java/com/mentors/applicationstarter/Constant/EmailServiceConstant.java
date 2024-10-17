package com.mentors.applicationstarter.Constant;

import org.springframework.beans.factory.annotation.Value;

public class EmailServiceConstant {

    public static final String MAIL_APPLICATION_BASE_URL = "http://localhost:8080" + "/api/v1/auth/confirm-password-reset";
    public static final String MAIL_APPLICATION_ACTIVATE_USER_URL = "http://localhost:8080" + "/api/v1/auth/activate?userUUID=";
    public static final String MAIL_APPLICATION_SUBJECT_NAME = "MůjPoukaz.cz - ";
    public static final String MAIL_APPLICATION_SUBJECT_NAME_ADMIN = "Admin MůjPoukaz.cz - ";
    public static final String MAIL_APPLICATION_ROOT_EMAIL = "test@mujpoukaz.cz";

    //REGISTER NEW USER MAIL
    public static final String MAIL_SUBJECT_REGISTER_NEW_USER = "Registrace nového uživatele";
    public static final String MAIL_SUBJECT_REGISTER_NEW_USER_CONFIRMATION = "Přihlašovací údaje";
    public static final String MAIL_SUBJECT_PASSWORD_RESET_REQUEST = "Žádost o reset uživatelského hesla";

    //ORDER MAIL
    public static final String MAIL_SUBJECT_ORDER_CLAIM_NEW_ORDER = "Nová objednávka";
    public static final String MAIL_SUBJECT_ORDER_ACCEPTED = "Objednávka byla přijata";
    public static final String MAIL_SUBJECT_ORDER_CANCELLED = "Objednávka byla zrušena";
    public static final String MAIL_SUBJECT_ORDER_SHIPPED = "Objednávka byla odeslána";

    //MAIL TEMPLATES
    public static final String MAIL_TEMPLATE_REGISTER_NEW_USER_CUSTOMER = "mail-register-new-user-customer";
    public static final String MAIL_TEMPLATE_REGISTER_NEW_USER_CONFIRMATION = "mail-register-new-user-confirmation";
    public static final String MAIL_TEMPLATE_REGISTER_NEW_USER_ADMINISTRATOR = "mail-register-new-user-administrator";
    public static final String MAIL_TEMPLATE_RESET_USER_PASSWORD_REQUEST = "mail-reset-password-request";
    public static final String MAIL_TEMPLATE_RESET_USER_PASSWORD_CONFIRMATION = "mail-reset-password-confirmation";
    public static final String MAIL_TEMPLATE_ORDER_CONFIRMATION = "mail-order-confirmation";
    public static final String MAIL_TEMPLATE_ORDER_ACCEPTED = "mail-order-accepted";
    public static final String MAIL_TEMPLATE_ORDER_CANCELLED = "mail-order-cancelled";
    public static final String MAIL_TEMPLATE_ORDER_SHIPPED = "mail-order-shipped";

}
