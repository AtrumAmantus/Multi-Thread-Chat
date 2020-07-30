package com.designwright.multithreadchat.server2.core;

import com.designwright.multithreadchat.server2.core.protocol.http.HttpHeader;
import com.designwright.multithreadchat.server2.core.protocol.http.HttpMethod;
import com.designwright.multithreadchat.server2.core.protocol.http.HttpRequest;
import com.designwright.multithreadchat.server2.core.protocol.http.HttpResponse;
import com.designwright.multithreadchat.server2.core.protocol.http.HttpStatusCode;
import com.designwright.multithreadchat.server2.core.protocol.ProtocolVersion;
import com.designwright.multithreadchat.server2.core.protocol.websocket.OpCode;
import com.designwright.multithreadchat.server2.core.protocol.websocket.WebSocketPacket;
import com.designwright.multithreadchat.server2.exception.HttpRequestException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Data
@Slf4j
public class Connection implements Runnable {

    private static final String WEBSOCKET_MAGIC_KEY = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    private final HttpSocketConnection socketConnection;
    private final WebSocketListener webSocketListener;

    @Override
    public void run() {
        try {
            createConnection(socketConnection);
        } catch (IOException e) {
            log.error("Failed to create client socket connection", e);
        } catch (HttpRequestException e) {
            log.error("HttpRequestException occurred", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("Invalid hashing algorithm", e);
        } catch (Exception e) {
            log.error("Uncaught exception", e);
        }
    }

    void createConnection(HttpSocketConnection socket) throws IOException, NoSuchAlgorithmException {
        Optional<HttpRequest> input = socket.read();
        if (input.isPresent()) {
            HttpRequest request = input.get();

            if (HttpMethod.GET.equals(request.getMethod())) {
                log.debug("Connection request made with GET");
                WebSocketConnection webSocketConnection = upgradeConnection(request, socket);
                webSocketConnection.write(
                        WebSocketPacket.createPacket(
                                true,
                                OpCode.TEXT,
                                false,
                                "{\"text\":\"Welcome to the server!\"}".getBytes()
                        )
                );
                webSocketListener.addSocket(new WebSocketConnection(socket));
            } else {
                log.debug("Connection request rejected, not GET");
            }
        } else {
            throw new HttpRequestException("Request contained no input");
        }
    }

    WebSocketConnection upgradeConnection(HttpRequest request, HttpSocketConnection socket) throws IOException, NoSuchAlgorithmException {
        String socketAcceptValue = Base64.getEncoder()
                .encodeToString(
                        MessageDigest.getInstance("SHA-1")
                                .digest(
                                        (request.getHeader(HttpHeader.SEC_WEBSOCKET_KEY) + WEBSOCKET_MAGIC_KEY)
                                                .getBytes(StandardCharsets.UTF_8.name())
                                )
                );

        HttpResponse response = HttpResponse.builder()
                .withProtocolVersion(ProtocolVersion.HTTP_1_1)
                .withHttpStatusCode(HttpStatusCode.SWITCHING_PROTOCOLS)
                .withHeader(HttpHeader.CONNECTION, "Upgrade")
                .withHeader(HttpHeader.UPGRADE, "websocket")
                .withHeader(HttpHeader.SEC_WEBSOCKET_ACCEPT, socketAcceptValue)
                .build();

        socket.write(response);

        return new WebSocketConnection(socket);
    }

}
