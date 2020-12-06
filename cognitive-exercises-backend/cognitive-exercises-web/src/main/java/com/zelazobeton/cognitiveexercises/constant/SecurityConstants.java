package com.zelazobeton.cognitiveexercises.constant;

public class SecurityConstants {
    private SecurityConstants(){};
    public static final String FORBIDDEN_MSG = "You need to log in to access this page";
    public static final String ACCESS_DENIED_MSG = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {
            "/user/**",
            "/portfolio/**",
            "/memory/**",
            "/games/**",
            "/token/refresh",
            "/token/delete",
    };
}
