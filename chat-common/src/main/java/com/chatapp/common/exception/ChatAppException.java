package com.chatapp.common.exception;

public class ChatAppException extends RuntimeException {
    private final String errorCode;

    public ChatAppException(String message) {
        super(message);
        this.errorCode = "GENERAL_ERROR";
    }

    public ChatAppException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ChatAppException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "GENERAL_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}
