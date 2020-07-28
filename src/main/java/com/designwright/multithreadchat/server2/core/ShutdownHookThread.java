package com.designwright.multithreadchat.server2.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ShutdownHookThread extends Thread {

    private final ShutdownHook shutdownHook;

    public void run() {
        log.debug("Executing shutdown hook...");
        shutdownHook.shutdown();
    }
}