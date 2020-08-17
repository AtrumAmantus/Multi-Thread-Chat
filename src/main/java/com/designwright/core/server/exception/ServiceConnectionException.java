package com.designwright.core.server.exception;

public class ServiceConnectionException extends RuntimeException {
    public ServiceConnectionException(String message) {
        super(message);
    }

    public ServiceConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
