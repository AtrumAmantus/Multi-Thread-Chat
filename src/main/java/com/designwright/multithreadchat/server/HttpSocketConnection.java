package com.designwright.multithreadchat.server;

import com.designwright.core.server.SocketConnection;
import com.designwright.multithreadchat.server.protocol.http.HttpDecoder;
import com.designwright.multithreadchat.server.protocol.http.HttpEncoder;
import com.designwright.multithreadchat.server.protocol.http.HttpRequest;
import com.designwright.multithreadchat.server.protocol.http.HttpResponse;

import java.net.Socket;

public class HttpSocketConnection extends SocketConnection<HttpRequest, HttpResponse> {

    public HttpSocketConnection(Socket socket) {
        super(socket, null, new HttpDecoder(), new HttpEncoder());
    }

}
