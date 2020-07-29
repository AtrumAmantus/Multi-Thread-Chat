package com.designwright.multithreadchat.server2.core.protocol.websocket;

import com.designwright.multithreadchat.server2.core.protocol.ProtocolDecoder;
import com.designwright.multithreadchat.server2.exception.WebsocketException;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class WebsocketDecoder implements ProtocolDecoder {

    @Override
    public String decode(ByteArrayOutputStream stream) {
        byte[] encodedData = stream.toByteArray();
        ByteBuffer decodedData;

        OpCode opCode = OpCode.getValueOf(encodedData[0] & 15);
        boolean isMasked = (encodedData[1] & 128) == 128;
        int payloadLength = encodedData[1] & 127;

        if (isMasked) {
            if (!OpCode.UNKNOWN.equals(opCode)) {
                byte[] mask;
                int payloadOffset;
                long dataLength = 0;
                //TODO: Rework to accommodate large payload sizes in a ByteBuffer
                if (payloadLength == 126) {
                    mask = new byte[] { encodedData[4], encodedData[5], encodedData[6], encodedData[7] };
                    payloadOffset = 8;
                    dataLength = ((encodedData[2] << 8) | encodedData[3]) + payloadLength;
                } else if (payloadLength == 127) {
                    mask = new byte[] { encodedData[10], encodedData[11], encodedData[12], encodedData[13] };
                    payloadOffset = 14;
                    for (int i = 0; i < 8; ++i) {
                        int leftShift = 8 - i;
                        dataLength += (encodedData[i] << leftShift);
                    }
                    dataLength += payloadOffset;
                } else {
                    mask = new byte[] { encodedData[2], encodedData[3], encodedData[4], encodedData[5] };
                    payloadOffset = 6;
                    dataLength = payloadLength + payloadOffset;
                }

                if (encodedData.length < dataLength) {
                    decodedData = ByteBuffer.allocate(0);
                } else {
                    decodedData = ByteBuffer.allocate(payloadLength);
                    for (int i = payloadOffset; i < dataLength; ++i) {
                        if (isMasked) {
                            int j = i - payloadOffset;
                            if (i < encodedData.length) {
                                decodedData.put((byte)(encodedData[i] ^ mask[j % 4]));
                            }
                        } else {
                            decodedData.put(encodedData[i]);
                        }
                    }
                }
            } else {
                throw new WebsocketException("Can't proceed, Invalid opcode.");
            }
        } else {
            throw new WebsocketException("Can't proceed, unmasked.");
        }

        return new String(decodedData.array());
    }

}
