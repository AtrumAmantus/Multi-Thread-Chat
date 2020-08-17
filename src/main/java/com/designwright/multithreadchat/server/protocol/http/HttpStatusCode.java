package com.designwright.multithreadchat.server.protocol.http;

import com.designwright.multithreadchat.exception.HttpRequestException;

import java.util.Optional;
import java.util.stream.Stream;

public enum HttpStatusCode {

    SWITCHING_PROTOCOLS(101, "Switching Protocols"),
    OK(200, "OK"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    UNAUTHORIZED(401, "Unauthorized");

    private final int code;
    private final String message;

    HttpStatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static HttpStatusCode getCode(int code) {
        Optional<HttpStatusCode> optional = Stream.of(HttpStatusCode.values()).filter(httpStatusCode -> httpStatusCode.code == code).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new HttpRequestException("Invalid status code " + code);
        }
    }

    @Override
    public String toString() {
        return code + " " + message;
    }
}
