package com.designwright.multithreadchat.server;

import com.designwright.core.container.Component;
import com.designwright.core.server.ConnectionSession;
import com.designwright.core.server.ConnectionSessionFactory;
import com.designwright.multithreadchat.data.domain.User;

@Component
public class ConnectionSessionFactoryImpl implements ConnectionSessionFactory<User> {

    @Override
    public ConnectionSession<User> create(User user) {
        ConnectionSession<User> connectionSession = new ConnectionSessionImpl(user);
        connectionSession.setValid(true);
        return connectionSession;
    }
}
