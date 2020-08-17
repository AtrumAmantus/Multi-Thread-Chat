package com.designwright.multithreadchat.config;

import com.designwright.multithreadchat.server.WebSocketListener;
import com.designwright.core.container.Bean;
import com.designwright.core.container.Configuration;
import com.designwright.core.server.SocketServerSettings;

import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("unused")
@Configuration
public class ApplicationConfiguration {

    @Bean
    SocketServerSettings socketServerSettings() {
        return SocketServerSettings.defaultSettings();
    }

    @Bean
    WebSocketListener webSocketListener() {
        return new WebSocketListener(new CopyOnWriteArrayList<>());
    }

}