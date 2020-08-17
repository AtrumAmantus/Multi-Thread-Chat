package com.designwright.core.server;

@FunctionalInterface
public interface ShutdownHook {
    void shutdown();
}