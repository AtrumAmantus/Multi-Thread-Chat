package com.designwright.multithreadchat.server2.core;

import com.designwright.multithreadchat.server2.exception.ServiceConnectionException;
import com.google.common.io.CharStreams;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@Slf4j
public class SocketListener {

    private boolean processSockets;
    private final List<SocketConnection> sockets;

    public SocketListener(List<SocketConnection> sockets) {
        this.sockets = sockets;
    }

    public void run() throws InterruptedException {
        processSockets = true;

        try {
            while (processSockets) {
                while (sockets.isEmpty()) {
                    Thread.sleep(100);
                }
                readData(sockets);
                Thread.sleep(1);
            }
        } finally {
            shutdownSockets();
        }
    }

    public void addSocket(SocketConnection socket) {
        sockets.add(socket);
    }

    void readData(List<SocketConnection> sockets) {
        for (SocketConnection socket : sockets) {
            try {
                readData(socket);
            } catch (ServiceConnectionException e) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    log.error("Unable to close socket connection", ioException);
                }
                sockets.remove(socket);
            }
        }
    }

    void readData(SocketConnection socket) {
        //TODO: Read data from socket input stream
//        try (Reader reader = new BufferedReader(new InputStreamReader(socket.getInputStream().read(), Charset.forName(StandardCharsets.UTF_8.name())))){
        try {
            Optional<String> input = socket.read();
            if (input.isPresent()) {
                String text = input.get();
                log.debug("Message pulled: " + text);
            }
        } catch (SocketException e) {
            log.error("Lost socket connection");
            throw new ServiceConnectionException("Connection failure", e);
        } catch (IOException e) {
            log.error("Unable to read from input stream", e);
        }
    }

    public void shutdown() {
        log.debug("Shutting down socket listener");
        processSockets = false;
    }

    void shutdownSockets() {
        log.debug("Closing socket connections");
        Iterator<SocketConnection> iterator = sockets.iterator();
        SocketConnection socket;
        while (iterator.hasNext()) {
            socket = iterator.next();
            try {
                socket.close();
            } catch (IOException e) {
                log.error("Unable to close socket connection", e);
            }
            iterator.remove();
        }
    }

}
