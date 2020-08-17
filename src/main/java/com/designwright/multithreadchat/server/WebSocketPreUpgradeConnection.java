package com.designwright.multithreadchat.server;

import com.designwright.core.server.Connection;
import com.designwright.core.server.ConnectionSession;
import com.designwright.core.server.ConnectionSessionFactory;
import com.designwright.core.server.InvalidConnectionSession;
import com.designwright.multithreadchat.data.domain.User;
import com.designwright.multithreadchat.exception.AuthorizationException;
import com.designwright.multithreadchat.exception.HttpRequestException;
import com.designwright.multithreadchat.exception.ResourceNotFoundException;
import com.designwright.multithreadchat.server.protocol.ProtocolVersion;
import com.designwright.multithreadchat.server.protocol.http.HttpHeader;
import com.designwright.multithreadchat.server.protocol.http.HttpMethod;
import com.designwright.multithreadchat.server.protocol.http.HttpRequest;
import com.designwright.multithreadchat.server.protocol.http.HttpResponse;
import com.designwright.multithreadchat.server.protocol.http.HttpStatusCode;
import com.designwright.multithreadchat.server.protocol.websocket.OpCode;
import com.designwright.multithreadchat.server.protocol.websocket.WebSocketPacket;
import com.designwright.multithreadchat.service.AuthorizationService;
import com.designwright.multithreadchat.service.UserService;
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
public class WebSocketPreUpgradeConnection implements Connection {

    private static final String WEBSOCKET_MAGIC_KEY = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    private final HttpSocketConnection socketConnection;
    private final WebSocketListener webSocketListener;
    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final ConnectionSessionFactory<User> connectionSessionFactory;

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
                ConnectionSession<User> session = authorizeConnection(authorization);
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

    private ConnectionSession<User> authorizeConnection(String authorization) {
        ConnectionSession<User> connectionSession;
        String decoded = new String(Base64.getDecoder().decode(authorization));
        String[] bits = decoded.split(":");

        if (bits.length == 2) {
            try {
                User user = authorizeUser(bits[0], bits[1]);
                connectionSession = connectionSessionFactory.create(user);
            } catch (AuthorizationException e) {
                log.debug("Could not authorize user: " + e.getMessage());
                connectionSession = new InvalidConnectionSession<>();
            }
        } else {
            connectionSession = new InvalidConnectionSession<>();
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

    WebSocketConnection upgradeConnection(HttpRequest request, HttpSocketConnection socket, ConnectionSession<User> session) throws IOException, NoSuchAlgorithmException {
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
