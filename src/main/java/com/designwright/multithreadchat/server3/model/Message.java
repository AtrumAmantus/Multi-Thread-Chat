package com.designwright.multithreadchat.server3.model;

import lombok.Data;

@Data
public class Message {
    private String from;
    private String to;
    private String content;
}