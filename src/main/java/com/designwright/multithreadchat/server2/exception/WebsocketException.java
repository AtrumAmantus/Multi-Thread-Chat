package com.designwright.multithreadchat.server2.exception;

public class WebsocketException extends ServiceConnectionException {
    public WebsocketException(String message) {
        super(message);
    }

    public WebsocketException(String message, Throwable cause) {
        super(message, cause);
    }
}
