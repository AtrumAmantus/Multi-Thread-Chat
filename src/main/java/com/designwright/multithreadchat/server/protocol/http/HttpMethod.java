package com.designwright.multithreadchat.server.protocol.http;

import com.designwright.multithreadchat.exception.HttpRequestMethodNotSupportedException;

import java.util.Optional;
import java.util.stream.Stream;

public enum HttpMethod {

    GET,
    POST,
    PUT,
    DELETE,
    HEAD;

    public static HttpMethod getValueOf(String methodName) {
        Optional<HttpMethod> optional = Stream.of(HttpMethod.values()).filter(httpMethod -> httpMethod.toString().equals(methodName)).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new HttpRequestMethodNotSupportedException(methodName + " not supported");
        }
    }

}
