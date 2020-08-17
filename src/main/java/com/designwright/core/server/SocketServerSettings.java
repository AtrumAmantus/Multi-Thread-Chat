package com.designwright.core.server;

import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
public class SocketServerSettings {

    private Integer port;
    private Integer minThreadPoolSize;
    private Integer maxThreadPoolSize;
    private Integer keepAliveTime;
    private TimeUnit timeUnit;

    public static SocketServerSettings defaultSettings() {
        SocketServerSettings serverSettings = new SocketServerSettings();
        serverSettings.port = 8080;
        serverSettings.minThreadPoolSize = 10;
        serverSettings.maxThreadPoolSize = 20;
        serverSettings.keepAliveTime = 60;
        serverSettings.timeUnit = TimeUnit.SECONDS;
        return serverSettings;
    }

}
