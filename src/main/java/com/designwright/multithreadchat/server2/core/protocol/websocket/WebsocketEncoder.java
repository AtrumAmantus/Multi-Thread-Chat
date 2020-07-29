package com.designwright.multithreadchat.server2.core.protocol.websocket;

import com.designwright.multithreadchat.server2.core.protocol.ProtocolEncoder;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class WebsocketEncoder implements ProtocolEncoder {

    private static final int BITS_16 = 65_535;
    private static final int BITS_8 = 125;

    @Override
    public byte[] encode(String message) throws UnsupportedEncodingException {
        int dataLength;
        byte[] decodedData = message.getBytes(StandardCharsets.UTF_8.name());
        OpCode opCode = OpCode.TEXT;
        boolean isMasked = false;

        byte[] payloadLengthBytes;

        byte maskedBit = (byte)(isMasked ? -127 : 0);
        if (decodedData.length > BITS_16) {
            payloadLengthBytes = new byte[] {
                    (byte)(maskedBit | 127),
                    (byte)((decodedData.length >> 24) & 127),
                    (byte)((decodedData.length >> 16) & 127),
                    (byte)((decodedData.length >> 8) & 127),
                    (byte)(decodedData.length & 127)
            };
        } else if (decodedData.length > BITS_8) {
            payloadLengthBytes = new byte[] {
                    (byte)(maskedBit | 126),
                    (byte)((decodedData.length >> 8) & 127),
                    (byte)((decodedData.length >> 16) & 127),
                    (byte)(decodedData.length & 127)
            };
        } else {
            payloadLengthBytes = new byte[] {
                    (byte)(maskedBit | decodedData.length)
            };
        }

        byte[] maskBytes;
        if (isMasked) {
            maskBytes = new byte[4];
            new Random().nextBytes(maskBytes);
        } else {
            maskBytes = new byte[0];
        }

        dataLength = payloadLengthBytes.length + maskBytes.length + decodedData.length + 1; // +1 for fin/opcode

        //TODO: Rework to accommodate large payload sizes in a ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(dataLength);
        buffer.put((byte)((byte)-127 | opCode.getCode()));  // fin bit, reserved x 3, & opcode
        buffer.put(payloadLengthBytes);
        buffer.put(maskBytes);

        if (isMasked) {
            for (int i = 0; i < decodedData.length; ++i) {
                buffer.put((byte)(decodedData[i] ^ maskBytes[i % 4]));
            }
        } else {
            buffer.put(decodedData);
        }

        return buffer.array();
    }

}
