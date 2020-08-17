package com.designwright.multithreadchat.server2.core;

import com.designwright.multithreadchat.server2.loader.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Component
public class ServerRunner {

    private final List<Server> serverList;
    private final List<StoppableThread> serverThreads;

    public ServerRunner(List<Server> serverList) {
        this.serverList = serverList;
        this.serverThreads = new ArrayList<>(serverList.size());
    }

    @PostConstruct
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
