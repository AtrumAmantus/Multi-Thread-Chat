package com.designwright.multithreadchat.server;

import com.designwright.multithreadchat.server.protocol.websocket.WebSocketPacket;
import com.designwright.multithreadchat.server.protocol.websocket.OpCode;
import com.designwright.core.server.exception.ServiceConnectionException;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@Slf4j
public class WebSocketListener {

    private boolean processSockets;
    private final List<WebSocketConnection> sockets;

    public WebSocketListener(List<WebSocketConnection> sockets) {
        this.sockets = sockets;
    }

    public void run() {
        processSockets = true;

        try {
            while (processSockets) {
                while (sockets.isEmpty()) {
                    Thread.sleep(100);
                }
                readData(sockets);
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (log.isDebugEnabled()) {
                log.debug("Thread interrupted for WebSocketListener");
            }
            // Proceed to shut down.
        } finally {
            shutdownSockets();
        }
    }

    public void addSocket(WebSocketConnection socket) {
        sockets.add(socket);
    }

    void readData(List<WebSocketConnection> sockets) {
        for (WebSocketConnection socket : sockets) {
            try {
                readData(socket);
            } catch (ServiceConnectionException e) {
                socket.close();
                sockets.remove(socket);
            }
        }
    }

    void readData(WebSocketConnection socket) {
        try {
            Optional<WebSocketPacket> input = socket.read();
            if (input.isPresent()) {
                String text = new String(input.get().getPayload());
                log.debug("Message pulled: " + text);
                socket.write(
                        WebSocketPacket.createPacket(
                                true,
                                OpCode.TEXT,
                                false,
                                ("{\"text\":\"You said, '" + text + "'\"}").getBytes()
                        )
                );
            }
        } catch (SocketException e) {
            log.error("Lost socket connection");
            throw new ServiceConnectionException("Connection failure", e);
        } catch (IOException e) {
            log.error("Unable to read from input stream", e);
        }
    }

    public void shutdown() {
        if (log.isDebugEnabled()) {
            log.debug("Shutting down WebSocketListener");
        }
        processSockets = false;
    }

    void shutdownSockets() {
        log.debug("Closing socket connections");
        Iterator<WebSocketConnection> iterator = sockets.iterator();
        WebSocketConnection socket;
        while (iterator.hasNext()) {
            socket = iterator.next();
            socket.close();
            iterator.remove();
        }
    }

}
