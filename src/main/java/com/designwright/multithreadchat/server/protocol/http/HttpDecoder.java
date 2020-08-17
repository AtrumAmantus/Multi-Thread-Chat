package com.designwright.multithreadchat.server.protocol.http;

import com.designwright.core.server.ProtocolDecoder;

import java.io.ByteArrayOutputStream;

public class HttpDecoder implements ProtocolDecoder<HttpRequest> {

    @Override
    public HttpRequest decode(ByteArrayOutputStream stream) {
        return HttpRequest.create(stream.toString());
    }

}
