package com.designwright.core.server;

import com.designwright.core.server.exception.ServiceConnectionException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.util.Optional;

@RequiredArgsConstructor
@EqualsAndHashCode
@Slf4j
public abstract class SocketConnection<T, R> implements Closeable {

    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    protected final ProtocolDecoder<T> decoder;
    protected final ProtocolEncoder<R> encoder;
    @Getter
    private final ConnectionSession<? extends SessionUser> session;

    public SocketConnection(Socket socket, ConnectionSession<? extends SessionUser> session, ProtocolDecoder<T> decoder, ProtocolEncoder<R> encoder) {
        this.socket = socket;
        this.session = session;
        this.encoder = encoder;
        this.decoder = decoder;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new ServiceConnectionException("Unable to acquire stream", e);
        }
    }

    public SocketConnection(SocketConnection<?,?> socket, ConnectionSession<? extends SessionUser> session, ProtocolDecoder<T> decoder, ProtocolEncoder<R> encoder) {
        this.socket = socket.socket;
        this.session = session;
        this.inputStream = socket.inputStream;
        this.outputStream = socket.outputStream;
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public Optional<T> read() throws IOException {
        Optional<T> content;
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        while (inputStream.available() > 0) {
            int length = inputStream.read(buffer);
            out.write(buffer, 0, length);
        }

        if (out.size() > 0) {
            content = Optional.of(decoder.decode(out));
        } else {
            content = Optional.empty();
        }

        out.close();
        return content;
    }

    public void write(R object) throws IOException {
        try {
            byte[] byteMessage = encoder.encode(object);
            outputStream.write(byteMessage, 0, byteMessage.length);
        } catch (BufferOverflowException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() {
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

}
