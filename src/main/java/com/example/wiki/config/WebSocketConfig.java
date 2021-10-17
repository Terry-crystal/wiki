package com.example.wiki.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author Crystal
 * @version 1.0
 * @date 2021/10/17 17:01
 */

@Configuration
public class WebSocketConfig {

    //声明我这个SpringBoot应用要暴露，使用WebSocket了

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
