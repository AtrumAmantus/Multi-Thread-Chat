package com.designwright.multithreadchat.server2.core;

import com.designwright.multithreadchat.server2.exception.InternalServiceException;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class WebSocketServer {

    private ExecutorService clientExecutor;
    private WebSocketConnectionFactory webSocketConnectionFactory;
    private SocketListener socketListener;
    private final int port;
    private boolean keepRunning;

    public WebSocketServer(WebSocketConnectionFactory webSocketConnectionFactory, int port) {
        BlockingQueue<Runnable> boundedQueue = new ArrayBlockingQueue<>(1000);
        this.clientExecutor = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS, boundedQueue, new ThreadPoolExecutor.AbortPolicy());
        this.webSocketConnectionFactory = webSocketConnectionFactory;
        this.port = port;
        keepRunning = true;
    }

    public void start() {
        ServerSocket server;
        clientExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    socketListener.run();
                } catch (InterruptedException e) {
                    // Exit the thread loop
                }
            }
        });
        try {
            server = createServerSocket(port);
            acceptClientConnections(server);
        } catch (InternalServiceException e) {
            log.error("Service failed unrecoverably", e);
        }
    }

    ServerSocket createServerSocket(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            log.info("Server started on 127.0.0.1:" + port);
            log.info("Waiting for connections..");

            return serverSocket;
        } catch (IOException e) {
            throw new InternalServiceException("Unable to start server on port " + port, e);
        }
    }

    void acceptClientConnections(ServerSocket server) {
        do {
            try {
                Socket client = server.accept();
                clientExecutor.submit(webSocketConnectionFactory.webSocketConnection(client));
            } catch (IOException e) {
                log.error("Client failed to connect", e);
            }
        } while (keepRunning);
    }

}
