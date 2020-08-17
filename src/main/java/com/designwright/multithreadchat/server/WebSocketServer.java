package com.designwright.multithreadchat.server;

import com.designwright.core.server.SocketServer;
import com.designwright.core.server.SocketServerSettings;
import com.designwright.core.server.Stoppable;
import com.designwright.core.server.StoppableThread;
import com.designwright.core.container.Component;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EqualsAndHashCode(callSuper = true)
public class WebSocketServer extends SocketServer {

    private final StoppableThread webSocketListenerThread;

    public WebSocketServer(ConnectionFactoryImpl connectionFactory, SocketServerSettings settings) {
        super(connectionFactory, settings);
        webSocketListenerThread = new StoppableThread(new Stoppable() {
            @Override
            public void stop() {
                connectionFactory.getWebSocketListener().shutdown();
            }

            @Override
            public void run() {
                connectionFactory.getWebSocketListener().run();
            }
        });
    }

    @Override
    public void setup() {
        webSocketListenerThread.start();
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        webSocketListenerThread.stop();
    }
}
