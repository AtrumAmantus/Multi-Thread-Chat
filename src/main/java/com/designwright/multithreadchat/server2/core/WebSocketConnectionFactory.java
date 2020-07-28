package com.designwright.multithreadchat.server2.core;

import com.designwright.multithreadchat.server2.core.protocol.http.HttpRequestFactory;
import lombok.Data;

import java.net.Socket;

@Data
public class WebSocketConnectionFactory {
    
    private final SocketListener socketListener;
    private final HttpRequestFactory httpRequestFactory;
    
    public WebSocketConnection webSocketConnection(Socket socket) {
        return new WebSocketConnection(new SocketConnection(socket), socketListener, httpRequestFactory);
    }

}
