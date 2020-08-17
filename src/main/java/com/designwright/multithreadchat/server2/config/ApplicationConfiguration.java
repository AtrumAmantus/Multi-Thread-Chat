package com.designwright.multithreadchat.server2.config;

import com.designwright.multithreadchat.server2.core.SocketServerSettings;
import com.designwright.multithreadchat.server2.core.WebSocketListener;
import com.designwright.multithreadchat.server2.loader.Bean;
import com.designwright.multithreadchat.server2.loader.Configuration;

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