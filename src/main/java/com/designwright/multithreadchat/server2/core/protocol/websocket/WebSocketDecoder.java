package com.designwright.multithreadchat.server2.core.protocol.websocket;

import com.designwright.multithreadchat.server2.core.protocol.ProtocolDecoder;
import com.designwright.multithreadchat.server2.exception.WebsocketException;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class WebSocketDecoder implements ProtocolDecoder<WebSocketPacket> {

    private static final byte OPCODES = 15;
    private static final long LENGTH_64BIT = 127;
    private static final long LENGTH_16BIT = 126;

    @Override
    public WebSocketPacket decode(ByteArrayOutputStream stream) {
        byte[] encodedData = stream.toByteArray();
        ByteBuffer decodedData;

        boolean isFin = encodedData[0] < 0;
        OpCode opCode = OpCode.getValueOf(encodedData[0] & OPCODES);
        boolean isMasked = encodedData[1] < 0;
        byte payloadLengthBits = (byte)(encodedData[1] & 127);
        byte[] mask;

        if (!OpCode.UNKNOWN.equals(opCode)) {
            int lengthFramesOffset;
            int payloadOffset;
            int opCodeLengthBitsOffset = 2;
            long payloadLength = 0;
            long packetLength = 0;
            //TODO: Rework to accommodate large payload sizes in a ByteBuffer
            if (payloadLengthBits == LENGTH_16BIT) {
                lengthFramesOffset = 2;
                payloadLength = ((encodedData[2] << 8) | encodedData[3]);
            } else if (payloadLengthBits == LENGTH_64BIT) {
                lengthFramesOffset = 8;
                for (int i = 0; i < 8; ++i) {
                    int leftShift = 8 - i;
                    payloadLength += (encodedData[i] << leftShift);
                }
            } else {
                lengthFramesOffset = 0;
                payloadLength = payloadLengthBits;
            }

            if (isMasked) {
                mask = new byte[] {
                        encodedData[lengthFramesOffset + 2],
                        encodedData[lengthFramesOffset + 3],
                        encodedData[lengthFramesOffset + 4],
                        encodedData[lengthFramesOffset + 5]
                };
            } else {
                mask = new byte[0];
            }

            payloadOffset = opCodeLengthBitsOffset + lengthFramesOffset + mask.length;
            packetLength = payloadOffset + payloadLength;

            if (encodedData.length != packetLength) {
                throw new WebsocketException("Specified payload length does not match actual");
            } else {
                //TODO: Fix this temporary conversion to int
                decodedData = ByteBuffer.allocate((int)payloadLength);
                for (int i = payloadOffset; i < packetLength; ++i) {
                    if (isMasked) {
                        int payloadByteIndex = i - payloadOffset;
                        if (i < encodedData.length) {
                            decodedData.put((byte)(encodedData[i] ^ mask[payloadByteIndex % 4]));
                        }
                    } else {
                        decodedData.put(encodedData[i]);
                    }
                }
            }
        } else {
            throw new WebsocketException("Can't proceed, Invalid opcode.");
        }

        return WebSocketPacket.createPacket(isFin, opCode, mask, decodedData.array());
    }

}
