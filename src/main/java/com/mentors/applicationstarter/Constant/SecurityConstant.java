package com.mentors.applicationstarter.Constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 86_400_000; //24Hours Token Expiration time in miliseconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String JWT_TOKEN_SECRET = "544D4B62576358367976794D335757447842566A7478753441746A796C3177616F4C2F584674592F466C55434C74763332325276664346585949633252775A42";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String TOKEN_ISSUER_NAME = "E-Voucher";
    public static final String TOKEN_ADMINISTRATION = "E-Voucher User Management Service";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page/resource";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this resource";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    //public static final String[] PUBLIC_URLS = {"/status/**","/user/login","/user/list", "/user/register", "/user/resetpassword/**", "/user/image/**"}; //allowed url to be accessed without permissions
    public static final String[] PUBLIC_URLS = {"*"}; // All Public

    public static final String PASSOWRD_GENERATOR_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
    public static Integer PASSWORD_GENERATOR_LENGTH = 32;


    public static final String AES_SECURITY_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final String AES_SECURITY_INITIALIZATION_VECTOR = "ZW5jcnlwdGlvbkludFZlYw";
    public static final String AES_SECURITY_SECRET_KEY = "YWVzRW5jcnlwdGlvbktleQ";


}
