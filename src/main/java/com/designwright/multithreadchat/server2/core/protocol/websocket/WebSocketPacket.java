package com.designwright.multithreadchat.server2.core.protocol.websocket;

import lombok.Data;

import java.util.Random;

@Data
public class WebSocketPacket {

    private boolean fin;
    private OpCode opCode;
    private boolean masked;
    private byte[] mask;
    private byte[] payload;

    private WebSocketPacket() {

    }

    public static WebSocketPacket createPacket(boolean fin, OpCode opCode, boolean maskPayload, byte[] payload) {
        WebSocketPacket webSocketPacket;

        if (maskPayload) {
            byte[] mask = new byte[4];
            new Random().nextBytes(mask);
            webSocketPacket = createPacket(fin, opCode, mask, payload);
        } else {
            webSocketPacket = createPacket(fin, opCode, null, payload);
        }

        return webSocketPacket;
    }

    public static WebSocketPacket createPacket(boolean fin, OpCode opCode, byte[] mask, byte[] payload) {
        WebSocketPacket webSocketPacket = new WebSocketPacket();
        webSocketPacket.fin = fin;
        webSocketPacket.opCode = opCode;
        webSocketPacket.masked = (mask != null && mask.length != 0);
        webSocketPacket.mask = mask;
        webSocketPacket.payload = payload;
        return webSocketPacket;
    }

}
