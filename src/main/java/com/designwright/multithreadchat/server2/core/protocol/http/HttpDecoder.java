package com.designwright.multithreadchat.server2.core.protocol.http;

import com.designwright.multithreadchat.server2.core.protocol.ProtocolDecoder;

import java.io.ByteArrayOutputStream;

public class HttpDecoder implements ProtocolDecoder {

    @Override
    public String decode(ByteArrayOutputStream stream) {
        return stream.toString();
    }

}
