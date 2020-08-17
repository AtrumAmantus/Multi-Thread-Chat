package com.designwright.multithreadchat.server.protocol.websocket;

import lombok.Getter;

public enum OpCode {
    TEXT(1),
    BINARY(2),
    CLOSE(8),
    PING(9),
    PONG(10),
    UNKNOWN(-1);

    @Getter
    private final int code;

    OpCode(int code) {
        this.code = code;
    }

    public static OpCode getValueOf(int value) {
        OpCode opCode = UNKNOWN;

        for (OpCode opCode1 : OpCode.values()) {
            if (opCode1.code == value) {
                opCode = opCode1;
            }
        }

        return opCode;
    }
}
