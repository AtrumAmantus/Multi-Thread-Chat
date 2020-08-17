package com.designwright.multithreadchat.server;

import com.designwright.core.server.Connection;
import com.designwright.core.server.ConnectionFactory;
import com.designwright.core.server.ConnectionSessionFactory;
import com.designwright.multithreadchat.data.domain.User;
import com.designwright.multithreadchat.service.UserService;
import com.designwright.core.container.Component;
import com.designwright.multithreadchat.service.AuthorizationService;
import lombok.Data;

import java.net.Socket;

@Data
@Component
public class ConnectionFactoryImpl implements ConnectionFactory {

    private final WebSocketListener webSocketListener;
    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final ConnectionSessionFactory<User> connectionSessionFactory;
    
    public Connection connection(Socket socket) {
        return new WebSocketPreUpgradeConnection(new HttpSocketConnection(socket), webSocketListener, userService, authorizationService, connectionSessionFactory);
    }

}
