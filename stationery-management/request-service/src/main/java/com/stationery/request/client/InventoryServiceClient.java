package com.stationery.request.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


//since services need to communicate with one another, over the network, this package is needed.

//this client communicates with the inventory service to perform inventory checks and adjustments.
@FeignClient(name = "inventory-service")   //name of the service, registered on eureka
public interface InventoryServiceClient {

    //If you execute getItemsById(5), Feign will construct and send this request behind the scene to inventory-service.
    @GetMapping("/api/inventory/{id}")    //this is the URL path of the endpoint in inventory-service. @GetMapping is a spring annotation, telling Feign that this method triggers a get request
    Object getItemById(@PathVariable("id") Long id);    //@PathVariable links the Java parameter id to {id}, which is dynamic placeholder.

    //deduct stock
    //when deducting stock, it will call this so that the same can be updated in inventory. 
    @PutMapping("/api/inventory/{id}/deduct")
    void deductStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);
}