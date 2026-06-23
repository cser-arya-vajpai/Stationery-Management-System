package com.stationery.request.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

//this client communicates with the auth-service.
@FeignClient(name = "auth-service")  //specifies the name of the service that we need to communicate with, in Eureka server
public interface AuthServiceClient {

    @GetMapping("/api/auth/health")    //in current code, we are not invoking auth-service anywhere, but this exists for later implementations. If we want to add features later that require communication with auth-service, we already have this.
    String checkAuthServiceHealth();
}