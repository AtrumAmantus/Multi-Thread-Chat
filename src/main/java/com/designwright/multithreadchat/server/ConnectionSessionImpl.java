package com.designwright.multithreadchat.server;

import com.designwright.core.server.ConnectionSession;
import com.designwright.multithreadchat.data.domain.User;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ConnectionSessionImpl extends ConnectionSession<User> {

    public ConnectionSessionImpl(User user) {
        super(user);
    }

}
