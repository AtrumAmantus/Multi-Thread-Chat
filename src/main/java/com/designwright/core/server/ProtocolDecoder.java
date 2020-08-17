package com.designwright.core.server;

import java.io.ByteArrayOutputStream;

public interface ProtocolDecoder<T> {

    T decode(ByteArrayOutputStream stream);

}
