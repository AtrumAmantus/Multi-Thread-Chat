package com.designwright.multithreadchat.server2.core;

@FunctionalInterface
public interface ShutdownHook {
    void shutdown();
}