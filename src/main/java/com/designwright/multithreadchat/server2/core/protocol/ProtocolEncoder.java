package com.designwright.multithreadchat.server2.core.protocol;

import java.io.UnsupportedEncodingException;

public interface ProtocolEncoder {

    byte[] encode(String message) throws UnsupportedEncodingException;

}
