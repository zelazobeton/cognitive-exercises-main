package com.zelazobeton.cognitiveexercieses.constant;

public class TokenConstant {
    private TokenConstant(){};
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 5_000; // milliseconds
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 432_000_000; // milliseconds
    public static final String JWT_REFRESH_TOKEN_HEADER = "Jwt-Refresh-Token";
    public static final String TOKEN_ISSUER = "Cognitive Exercises";
    public static final String TOKEN_CANNOT_BE_VERIFIED_MSG = "Token cannot be verified";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
}
