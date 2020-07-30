package com.designwright.multithreadchat.server2.core.protocol;

import java.io.UnsupportedEncodingException;

public interface ProtocolEncoder<T> {

    byte[] encode(T t) throws UnsupportedEncodingException;

}
