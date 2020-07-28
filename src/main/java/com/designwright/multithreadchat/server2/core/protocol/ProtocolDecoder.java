package com.designwright.multithreadchat.server2.core.protocol;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public interface ProtocolDecoder {

    String decode(ByteArrayOutputStream stream);

}
