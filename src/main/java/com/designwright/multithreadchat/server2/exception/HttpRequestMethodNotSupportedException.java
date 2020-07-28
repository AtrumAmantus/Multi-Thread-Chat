package com.designwright.multithreadchat.server2.exception;

public class HttpRequestMethodNotSupportedException extends HttpRequestException {
    public HttpRequestMethodNotSupportedException(String message) {
        super(message);
    }

    public HttpRequestMethodNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
