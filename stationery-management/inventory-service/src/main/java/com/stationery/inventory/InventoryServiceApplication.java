package com.stationery.inventory;
//Entry point of the service 

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

//! Role of this service: Manage the catalog of stationery items and track their quantities. 

@SpringBootApplication   //@Configuration + @EnableAutoConfiguration + @ComponentScan
@EnableDiscoveryClient   //register this application with the configured service registry(Eureka server).
@EnableFeignClients     //This tells spring to scan for interfaces annotated with @FeignClientsand generate working HTTP-client implementations for them at startup.
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}