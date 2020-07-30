package com.designwright.multithreadchat.server2.core;

import lombok.Data;

import java.net.Socket;

@Data
public class ConnectionFactory {
    
    private final WebSocketListener webSocketListener;
    
    public Connection connection(Socket socket) {
        return new Connection(new HttpSocketConnection(socket), webSocketListener);
    }

}
