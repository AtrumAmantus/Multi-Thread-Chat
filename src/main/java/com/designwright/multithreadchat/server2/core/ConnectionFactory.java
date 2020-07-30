package com.designwright.multithreadchat.server2.core;

import com.designwright.multithreadchat.server2.service.AuthorizationService;
import com.designwright.multithreadchat.server2.service.UserService;
import lombok.Data;

import java.net.Socket;

@Data
public class ConnectionFactory {
    
    private final WebSocketListener webSocketListener;
    private final UserService userService;
    private final AuthorizationService authorizationService;
    
    public Connection connection(Socket socket) {
        return new Connection(new HttpSocketConnection(socket), webSocketListener, userService, authorizationService);
    }

}
