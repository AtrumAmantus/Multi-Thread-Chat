package com.designwright.core.server;

import com.designwright.core.server.exception.InternalServiceException;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public abstract class SocketServer extends Server {

    private final ExecutorService clientExecutor;
    private final ConnectionFactory connectionFactory;
    private final int port;

    private ServerSocket serverSocket;

    public SocketServer(ConnectionFactory connectionFactory, SocketServerSettings settings) {
        super();
        BlockingQueue<Runnable> boundedQueue = new ArrayBlockingQueue<>(1000);
        this.clientExecutor = new ThreadPoolExecutor(
                settings.getMinThreadPoolSize(),
                settings.getMaxThreadPoolSize(),
                settings.getKeepAliveTime(),
                settings.getTimeUnit(),
                boundedQueue,
                new ThreadPoolExecutor.AbortPolicy()
        );
        this.connectionFactory = connectionFactory;
        this.port = settings.getPort();
    }

    @SuppressWarnings("unused")
    @PostConstruct
    public void init() {
        setup();
        try {
            serverSocket = createServerSocket(port);
        } catch (InternalServiceException e) {
            log.error("Service failed and could not recover", e);
        }
        log.info("SocketServer started on port " + port);
    }

    public abstract void setup();

    ServerSocket createServerSocket(int port) {
        try {
            log.info("Server starting on 127.0.0.1:" + port);

            return new ServerSocket(port);
        } catch (IOException e) {
            throw new InternalServiceException("Unable to start server on port " + port, e);
        }
    }

    public void loop() {
        try {
            Socket socket = serverSocket.accept();
            clientExecutor.submit(connectionFactory.connection(socket));
        } catch (IOException e) {
            log.error("Client failed to connect", e);
        }
    }

    @Override
    protected void shutdown() {
        clientExecutor.shutdown();
    }
}
