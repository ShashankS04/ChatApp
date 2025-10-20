package com.chatapp.common.constants;

public class ApiConstants {
    public static final String API_VERSION = "/api/v1";
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // API Paths
    public static final String AUTH_PATH = API_VERSION + "/auth";
    public static final String USERS_PATH = API_VERSION + "/users";
    public static final String CHAT_PATH = API_VERSION + "/chat";
    public static final String MEDIA_PATH = API_VERSION + "/media";

    private ApiConstants() {}
}
