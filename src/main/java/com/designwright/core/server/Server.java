package com.designwright.core.server;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode
public abstract class Server implements Stoppable {

    private boolean keepRunning = true;

    @Override
    public void run() {
        log.info("Server is now running...");
        while (keepRunning) {
            try {
                loop();
                Thread.sleep(1);
            } catch (InterruptedException e) {
                log.error("Server thread was interrupted", e);
                Thread.currentThread().interrupt();
                stop();
            } catch (Exception e) {
                log.error("Uncaught exception", e);
            }
        }
    }

    public abstract void loop();

    public final void stop() {
        shutdown();
        keepRunning = false;
    }

    protected abstract void shutdown();

}
