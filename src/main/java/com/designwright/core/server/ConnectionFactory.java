package com.designwright.core.server;

import java.net.Socket;

public interface ConnectionFactory {

    Connection connection(Socket socket);

}
