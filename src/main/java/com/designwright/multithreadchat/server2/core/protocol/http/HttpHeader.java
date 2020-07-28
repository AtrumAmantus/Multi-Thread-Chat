package com.designwright.multithreadchat.server2.core.protocol.http;

import com.designwright.multithreadchat.server2.exception.HttpRequestException;

import java.util.Optional;
import java.util.stream.Stream;

public enum HttpHeader {

    HOST("Host"),
    CONNECTION("Connection"),
    PRAGMA("Pragma"),
    CACHE_CONTROL("Cache-Control"),
    USER_AGENT("User-Agent"),
    UPGRADE("Upgrade"),
    ORIGIN("Origin"),
    SEC_WEBSOCKET_VERSION("Sec-WebSocket-Version"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    SEC_WEBSOCKET_ACCEPT("Sec-WebSocket-Accept"),
    SEC_WEBSOCKET_EXTENSIONS("Sec-WebSocket-Extensions"),
    SEC_WEBSOCKET_KEY("Sec-WebSocket-Key");

    private final String prettyName;

    HttpHeader(String prettyName) {
        this.prettyName = prettyName;
    }

    HttpHeader getValueOf(String headerName) {
        Optional<HttpHeader> optional = Stream.of(HttpHeader.values()).filter(httpHeader -> httpHeader.prettyName.equals(headerName)).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new HttpRequestException("Invalid http header");
        }
    }

    @Override
    public String toString() {
        return prettyName;
    }
}
