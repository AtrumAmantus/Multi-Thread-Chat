package com.designwright.multithreadchat.exception;

import com.designwright.core.server.exception.ServiceConnectionException;

public class WebsocketException extends ServiceConnectionException {
    public WebsocketException(String message) {
        super(message);
    }

    public WebsocketException(String message, Throwable cause) {
        super(message, cause);
    }
}
