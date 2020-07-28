package com.designwright.multithreadchat.server2.core.protocol.http;

import com.designwright.multithreadchat.server2.exception.HttpRequestException;

public class HttpRequestFactory {

    public HttpRequest httpRequest(String httpData) {
        HttpRequest httpRequest = new HttpRequest(httpData);
        try {
            httpRequest.parseData();
        } catch (HttpRequestException e) {
            throw new HttpRequestException("Failed to parse http request", e);
        }
        return httpRequest;
    }

}
