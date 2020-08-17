package com.designwright.core.server;

public interface ConnectionSessionFactory<T extends SessionUser> {

    ConnectionSession<T> create(T user);

}
