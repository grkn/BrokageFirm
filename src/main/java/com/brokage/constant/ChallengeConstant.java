package com.brokage.constant;

public final class ChallengeConstant {


    public static final String ROLE_CUSTOMER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String DUMMY_SIGN = "anySign";
    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final long EXPIRE_TIME = 1000 * 60 * 60 * 2L;
    public static final String BASE_URL = "/api/v1";
    public static final String AUTHENTICATE_ENDPOINT = "/authenticate";
    public static final String TOKEN_ENDPOINT = "/token";
    private ChallengeConstant() {
    }
}
