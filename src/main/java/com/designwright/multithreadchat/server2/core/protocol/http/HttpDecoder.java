package com.designwright.multithreadchat.server2.core.protocol.http;

import com.designwright.multithreadchat.server2.core.protocol.ProtocolDecoder;

import java.io.ByteArrayOutputStream;

public class HttpDecoder implements ProtocolDecoder<HttpRequest> {

    @Override
    public HttpRequest decode(ByteArrayOutputStream stream) {
        return HttpRequest.create(stream.toString());
    }

}
