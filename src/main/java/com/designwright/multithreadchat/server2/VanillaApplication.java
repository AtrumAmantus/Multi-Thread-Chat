package com.designwright.multithreadchat.server2;

import com.designwright.multithreadchat.server2.core.ShutdownHookThread;
import com.designwright.multithreadchat.server2.core.WebSocketConnectionFactory;
import com.designwright.multithreadchat.server2.core.SocketListener;
import com.designwright.multithreadchat.server2.core.WebSocketServer;
import com.designwright.multithreadchat.server2.core.protocol.http.HttpRequestFactory;

import java.util.concurrent.CopyOnWriteArrayList;

public class VanillaApplication {

    public static void main(String[] args) {
        HttpRequestFactory httpRequestFactory = new HttpRequestFactory();
        SocketListener socketListener = new SocketListener(new CopyOnWriteArrayList<>());
        WebSocketConnectionFactory factory = new WebSocketConnectionFactory(socketListener, httpRequestFactory);
        WebSocketServer webSocketServer = new WebSocketServer(factory, 8080);

        Runtime current = Runtime.getRuntime();
        current.addShutdownHook(new ShutdownHookThread(socketListener::shutdown));

        webSocketServer.setSocketListener(socketListener);
        webSocketServer.start();
    }

}