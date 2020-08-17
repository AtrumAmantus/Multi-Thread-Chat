package com.designwright.core.server;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public abstract class ConnectionSession<T> {

    private boolean valid;
    private final T user;

}
