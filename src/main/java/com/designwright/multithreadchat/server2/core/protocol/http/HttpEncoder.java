package com.designwright.multithreadchat.server2.core.protocol.http;

import com.designwright.multithreadchat.server2.core.protocol.ProtocolEncoder;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class HttpEncoder implements ProtocolEncoder<HttpResponse> {

    @Override
    public byte[] encode(HttpResponse response) throws UnsupportedEncodingException {
        return response.getAsBytes(StandardCharsets.UTF_8);
    }

}
