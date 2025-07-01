package com.jesse.routerfunc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class WebClientConfig
{
    @Value(value = "${server.address}")
    private String SERVER_ADDRESS;

    @Value(value = "${server.port}")
    private String SERVER_PORT;

    /** 创建 WebClient。 */
    @Bean
    public WebClient webClient()
    {
        log.info("{} : {}", SERVER_ADDRESS, SERVER_PORT);

        // 设定 WebClient 的根请求目录
        return WebClient.create("http://" + SERVER_ADDRESS + ":" + SERVER_PORT);
    }
}