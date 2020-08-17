package com.designwright.multithreadchat.server.protocol.http;

import com.designwright.core.server.ProtocolEncoder;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class HttpEncoder implements ProtocolEncoder<HttpResponse> {

    @Override
    public byte[] encode(HttpResponse response) throws UnsupportedEncodingException {
        return response.getAsBytes(StandardCharsets.UTF_8);
    }

}
