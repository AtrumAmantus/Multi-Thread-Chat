package com.designwright.core.server;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class InvalidConnectionSession<T extends SessionUser> extends ConnectionSession<T> {

    public InvalidConnectionSession() {
        super(null);
        setValid(false);
    }

}
