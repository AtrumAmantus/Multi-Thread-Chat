package com.designwright.multithreadchat.server2;

import com.designwright.multithreadchat.server2.core.ShutdownHookThread;
import com.designwright.multithreadchat.server2.core.ConnectionFactory;
import com.designwright.multithreadchat.server2.core.WebSocketListener;
import com.designwright.multithreadchat.server2.core.WebSocketServer;
import com.designwright.multithreadchat.server2.data.repository.UserRepository;
import com.designwright.multithreadchat.server2.data.repository.UserRepositoryImpl;
import com.designwright.multithreadchat.server2.service.AuthorizationService;
import com.designwright.multithreadchat.server2.service.UserService;

import java.util.concurrent.CopyOnWriteArrayList;

public class VanillaApplication {

    public static void main(String[] args) {
        WebSocketListener webSocketListener = new WebSocketListener(new CopyOnWriteArrayList<>());
        UserRepository userRepository = new UserRepositoryImpl();
        UserService userService = new UserService(userRepository);
        AuthorizationService authorizationService = new AuthorizationService(userRepository);
        ConnectionFactory factory = new ConnectionFactory(webSocketListener, userService, authorizationService);
        WebSocketServer webSocketServer = new WebSocketServer(factory, 8080);

        Runtime current = Runtime.getRuntime();
        current.addShutdownHook(new ShutdownHookThread(webSocketListener::shutdown));

        webSocketServer.setWebSocketListener(webSocketListener);
        webSocketServer.start();
    }

}