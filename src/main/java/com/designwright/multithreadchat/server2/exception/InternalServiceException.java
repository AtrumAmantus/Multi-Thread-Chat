package com.designwright.multithreadchat.server2.exception;

public class InternalServiceException extends RuntimeException {
    public InternalServiceException(String message) {
        super(message);
    }

    public InternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
