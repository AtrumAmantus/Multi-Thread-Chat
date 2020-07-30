package com.designwright.multithreadchat.server2.core;

import com.designwright.multithreadchat.server2.data.domain.User;
import lombok.Data;

@Data
public class ConnectionSession {

    private User user;
    private boolean valid;

    public static final ConnectionSession INVALID = new ConnectionSession();

    static {
        INVALID.valid = false;
    }

    public static ConnectionSession create(User user) {
        ConnectionSession connectionSession = new ConnectionSession();
        connectionSession.user = user;
        connectionSession.valid = true;
        return connectionSession;
    }

}
