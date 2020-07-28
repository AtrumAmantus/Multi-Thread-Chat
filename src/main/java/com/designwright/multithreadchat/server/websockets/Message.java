package com.designwright.multithreadchat.server.websockets;

import lombok.Data;

@Data
public class Message {

    private String from;
    private String text;

}