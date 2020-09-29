package com.zelazobeton.cognitiveexercises.constant;

public class SecurityConstant {
    private SecurityConstant(){};

    public static final long EXPIRATION_TIME = 432_000_000; // milliseconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_ISSUER = "Cognitive Exercises";
    public static final String AUTHORITIES = "authorities";
    public static final String TOKEN_CANNOT_BE_VERIFIED_MSG = "Token cannot be verified";
    public static final String FORBIDDEN_MSG = "You need to log in to access this page";
    public static final String ACCESS_DENIED_MSG = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = { "/login", "/register" };
//    public static final String[] PUBLIC_URLS = { "**" };
}
