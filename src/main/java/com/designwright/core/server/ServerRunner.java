package com.designwright.core.server;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ServerRunner {

    private final List<Server> serverList;
    private final List<StoppableThread> serverThreads;

    public ServerRunner(List<Server> serverList) {
        this.serverList = serverList;
        this.serverThreads = new ArrayList<>(serverList.size());
    }

    public void init() {
        for (Server server : serverList) {
            StoppableThread serverThread = new StoppableThread(server);
            serverThread.start();
            this.serverThreads.add(serverThread);
        }
    }

    public void shutdown() {
        serverThreads.forEach(StoppableThread::stop);
    }

}
