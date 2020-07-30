package com.designwright.multithreadchat.server2.core.protocol;

import java.io.ByteArrayOutputStream;

public interface ProtocolDecoder<T> {

    T decode(ByteArrayOutputStream stream);

}
