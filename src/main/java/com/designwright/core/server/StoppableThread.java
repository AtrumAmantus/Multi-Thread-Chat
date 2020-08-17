package com.designwright.core.server;

public class StoppableThread {

    private final Stoppable stoppable;
    private final Thread thread;

    public StoppableThread(Stoppable stoppable) {
        this.stoppable = stoppable;
        this.thread = new Thread(stoppable);
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        stoppable.stop();
    }

}
