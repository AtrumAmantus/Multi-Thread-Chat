package com.designwright.multithreadchat.server2.core;

import com.designwright.multithreadchat.server2.core.protocol.http.HttpDecoder;
import com.designwright.multithreadchat.server2.core.protocol.http.HttpEncoder;
import com.designwright.multithreadchat.server2.core.protocol.http.HttpRequest;
import com.designwright.multithreadchat.server2.core.protocol.http.HttpResponse;

import java.net.Socket;

public class HttpSocketConnection extends SocketConnection<HttpRequest, HttpResponse> {

    public HttpSocketConnection(Socket socket) {
        super(socket, new HttpDecoder(), new HttpEncoder());
    }

}
