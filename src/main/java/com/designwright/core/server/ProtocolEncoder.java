package com.designwright.core.server;

import java.io.UnsupportedEncodingException;

public interface ProtocolEncoder<T> {

    byte[] encode(T t) throws UnsupportedEncodingException;

}
