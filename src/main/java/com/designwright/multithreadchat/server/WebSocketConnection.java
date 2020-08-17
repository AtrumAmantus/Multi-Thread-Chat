package com.designwright.multithreadchat.server;

import com.designwright.core.server.ConnectionSession;
import com.designwright.core.server.SocketConnection;
import com.designwright.multithreadchat.data.domain.User;
import com.designwright.multithreadchat.server.protocol.websocket.WebSocketDecoder;
import com.designwright.multithreadchat.server.protocol.websocket.WebSocketEncoder;
import com.designwright.multithreadchat.server.protocol.websocket.WebSocketPacket;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

@Slf4j
public class WebSocketConnection extends SocketConnection<WebSocketPacket, WebSocketPacket> {

    public WebSocketConnection(Socket socket, ConnectionSession<User> session) {
        super(socket, session, new WebSocketDecoder(), new WebSocketEncoder());
    }

    public WebSocketConnection(SocketConnection<?, ?> socket, ConnectionSession<User> session) {
        super(socket, session, new WebSocketDecoder(), new WebSocketEncoder());
    }

}
