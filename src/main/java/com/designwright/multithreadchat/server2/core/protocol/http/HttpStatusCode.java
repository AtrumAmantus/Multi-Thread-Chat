package com.designwright.multithreadchat.server2.core.protocol.http;

import com.designwright.multithreadchat.server2.exception.HttpRequestException;

import java.util.Optional;
import java.util.stream.Stream;

public enum HttpStatusCode {

    SWITCHING_PROTOCOLS(101, "Switching Protocols"),
    OK(200, "OK");

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
