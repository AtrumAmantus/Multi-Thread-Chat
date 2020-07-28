package com.designwright.multithreadchat.server2.core;

import com.designwright.multithreadchat.server2.core.protocol.http.HttpDecoder;
import com.designwright.multithreadchat.server2.core.protocol.ProtocolDecoder;
import com.designwright.multithreadchat.server2.core.protocol.ProtocolVersion;
import com.designwright.multithreadchat.server2.core.protocol.websocket.WebsocketDecoder;
import com.designwright.multithreadchat.server2.exception.ServiceConnectionException;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@EqualsAndHashCode
@Slf4j
public class SocketConnection implements Closeable {

    private ProtocolVersion protocolInUse;
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final Map<ProtocolVersion, ProtocolDecoder> decoders;

    public SocketConnection(Socket socket) {
        this.socket = socket;
        protocolInUse = ProtocolVersion.HTTP_1_1;
        decoders = new EnumMap<>(ProtocolVersion.class);
        decoders.put(ProtocolVersion.HTTP_1_1, new HttpDecoder());
        decoders.put(ProtocolVersion.WEBSOCKET_X, new WebsocketDecoder());
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new ServiceConnectionException("Unable to acquire stream", e);
        }
    }

    public Optional<String> read() throws IOException {
        Optional<String> content;
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        while (inputStream.available() > 0) {
            int length = inputStream.read(buffer);
            out.write(buffer, 0, length);
        }

        if (out.size() > 0) {
            content = Optional.of(decoders.get(protocolInUse).decode(out));
        } else {
            content = Optional.empty();
        }

        out.close();
        return content;
    }

    public void write(String message) throws IOException {
        byte[] byteMessage = message.getBytes(StandardCharsets.UTF_8.name());
        outputStream.write(byteMessage, 0, byteMessage.length);
    }

    @Override
    public void close() throws IOException {
        log.debug("Closing socket connection (" + socket.getLocalAddress().getHostAddress() + ")");
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            socket.close();
        } catch (IOException e) {
            log.error("Unable to close resource", e);
        }
    }

    public void upgradeConnection() {
        protocolInUse = ProtocolVersion.WEBSOCKET_X;
    }
}
