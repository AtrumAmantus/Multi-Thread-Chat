package com.designwright.multithreadchat.server2.core.protocol.websocket;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OpCode {
    TEXT(1),
    BINARY(2),
    CLOSE(8),
    PING(9),
    PONG(10),
    UNKNOWN(-1);

    private final int opCode;

    public static OpCode getValueOf(int value) {
        OpCode opCode = UNKNOWN;

        for (OpCode opCode1 : OpCode.values()) {
            if (opCode1.opCode == value) {
                opCode = opCode1;
            }
        }

        return opCode;
    }
}
