package com.designwright.multithreadchat.server2.core.protocol.http;

import com.designwright.multithreadchat.server2.core.protocol.ProtocolEncoder;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class HttpEncoder implements ProtocolEncoder {

    @Override
    public byte[] encode(String message) throws UnsupportedEncodingException {
        return message.getBytes(StandardCharsets.UTF_8.name());
    }

}
