package com.designwright.multithreadchat.server2.core;

import com.designwright.multithreadchat.server2.core.protocol.http.HttpHeader;
import com.designwright.multithreadchat.server2.core.protocol.http.HttpMethod;
import com.designwright.multithreadchat.server2.core.protocol.http.HttpRequest;
import com.designwright.multithreadchat.server2.core.protocol.http.HttpResponse;
import com.designwright.multithreadchat.server2.core.protocol.http.HttpStatusCode;
import com.designwright.multithreadchat.server2.core.protocol.ProtocolVersion;
import com.designwright.multithreadchat.server2.core.protocol.websocket.OpCode;
import com.designwright.multithreadchat.server2.core.protocol.websocket.WebSocketPacket;
import com.designwright.multithreadchat.server2.data.domain.User;
import com.designwright.multithreadchat.server2.exception.AuthorizationException;
import com.designwright.multithreadchat.server2.exception.HttpRequestException;
import com.designwright.multithreadchat.server2.exception.ResourceNotFoundException;
import com.designwright.multithreadchat.server2.service.AuthorizationService;
import com.designwright.multithreadchat.server2.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
    private final UserService userService;
    private final AuthorizationService authorizationService;

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
        if (log.isDebugEnabled()) {
            log.debug("Connection created successfully");
        }
    }

    void createConnection(HttpSocketConnection socket) throws IOException, NoSuchAlgorithmException {
        Optional<HttpRequest> input = socket.read();
        if (input.isPresent()) {
            HttpRequest request = input.get();

            String authorization = request.getUri().substring(1); // remove leading /
            if (!StringUtils.isEmpty(authorization)) {
                ConnectionSession session = authorizeConnection(authorization);
                if (session.isValid()) {
                    if (HttpMethod.GET.equals(request.getMethod())) {
                        log.debug("Connection request made with GET");
                        WebSocketConnection webSocketConnection = upgradeConnection(request, socket, session);
                        webSocketConnection.write(
                                WebSocketPacket.createPacket(
                                        true,
                                        OpCode.TEXT,
                                        false,
                                        "{\"text\":\"Welcome to the server!\"}".getBytes()
                                )
                        );
                        webSocketListener.addSocket(webSocketConnection);
                        if (log.isDebugEnabled()) {
                            log.debug("User connected");
                        }
                    } else {
                        log.debug("Connection request rejected, not GET");
                        socket.write(methodNotAllowedHttpResponse());
                        socket.close();
                    }
                } else {
                    log.debug("Invalid authorization");
                    socket.write(unauthorizedHttpResponse());
                    socket.close();
                }
            } else {
                log.debug("Unauthorized user, no auth header");
                socket.write(unauthorizedHttpResponse());
                socket.close();
            }
        } else {
            throw new HttpRequestException("Request contained no input");
        }
    }

    private ConnectionSession authorizeConnection(String authorization) {
        ConnectionSession connectionSession;
        String decoded = new String(Base64.getDecoder().decode(authorization));
        String[] bits = decoded.split(":");

        if (bits.length == 2) {
            try {
                User user = authorizeUser(bits[0], bits[1]);
                connectionSession = ConnectionSession.create(user);
            } catch (AuthorizationException e) {
                log.debug("Could not authorize user: " + e.getMessage());
                connectionSession = ConnectionSession.INVALID;
            }
        } else {
            connectionSession = ConnectionSession.INVALID;
        }

        return connectionSession;
    }

    private User authorizeUser(String email, String password) {
        User user;

        try {
            if (authorizationService.authorize(email, password)) {
                user = userService.getUserByEmail(email);
            } else {
                throw new AuthorizationException("Invalid Credentials");
            }
        } catch (ResourceNotFoundException e) {
            throw new AuthorizationException("User does not exist");
        }

        return user;
    }

    WebSocketConnection upgradeConnection(HttpRequest request, HttpSocketConnection socket, ConnectionSession session) throws IOException, NoSuchAlgorithmException {
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

        return new WebSocketConnection(socket, session);
    }

    public HttpResponse unauthorizedHttpResponse() {
        return httpResponse(HttpStatusCode.UNAUTHORIZED);
    }

    public HttpResponse methodNotAllowedHttpResponse() {
        return httpResponse(HttpStatusCode.METHOD_NOT_ALLOWED);
    }

    public HttpResponse httpResponse(HttpStatusCode code) {
        return  HttpResponse.builder()
                .withProtocolVersion(ProtocolVersion.HTTP_1_1)
                .withHttpStatusCode(code)
                .build();
    }

}
