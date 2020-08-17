package com.designwright.multithreadchat.server.protocol;

import com.designwright.multithreadchat.exception.HttpRequestException;

import java.util.Optional;
import java.util.stream.Stream;

public enum ProtocolVersion {
    HTTP_1_1("HTTP/1.1"),
    WEBSOCKET_X("websocket");

    private final String version;

    ProtocolVersion(String version) {
        this.version = version;
    }

    public static ProtocolVersion getValueOf(String protocolVersion) {
        Optional<ProtocolVersion> optional = Stream.of(ProtocolVersion.values()).filter(version -> version.toString().equals(protocolVersion)).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new HttpRequestException("Protocol Version " + protocolVersion + " not supported");
        }
    }

    @Override
    public String toString() {
        return this.version;
    }
}