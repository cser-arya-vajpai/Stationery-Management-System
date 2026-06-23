package com.stationery.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication    //this is the main entry point to start the app
@EnableConfigServer       //spring cloud instructs the spring boot to download all config-server libraries, activate config-server endpoints, and listen to microservics calling it.

public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}


