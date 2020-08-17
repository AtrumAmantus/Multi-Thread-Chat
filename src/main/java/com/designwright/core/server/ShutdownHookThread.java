package com.designwright.core.server;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ShutdownHookThread extends Thread {

    private final ShutdownHook shutdownHook;

    @Override
    public void run() {
        log.debug("Executing shutdown hook...");
        shutdownHook.shutdown();
    }
}