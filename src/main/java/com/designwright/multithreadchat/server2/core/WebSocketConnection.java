package com.designwright.multithreadchat.server2.core;

import com.designwright.multithreadchat.server2.core.protocol.websocket.WebSocketDecoder;
import com.designwright.multithreadchat.server2.core.protocol.websocket.WebSocketEncoder;
import com.designwright.multithreadchat.server2.core.protocol.websocket.WebSocketPacket;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

@Slf4j
public class WebSocketConnection extends SocketConnection<WebSocketPacket, WebSocketPacket> {

    public WebSocketConnection(Socket socket, ConnectionSession session) {
        super(socket, session, new WebSocketDecoder(), new WebSocketEncoder());
    }

    public WebSocketConnection(SocketConnection<?, ?> socket, ConnectionSession session) {
        super(socket, session, new WebSocketDecoder(), new WebSocketEncoder());
    }

}
