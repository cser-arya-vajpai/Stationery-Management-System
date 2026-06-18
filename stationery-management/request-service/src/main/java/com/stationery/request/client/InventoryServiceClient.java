package com.stationery.request.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service")
public interface InventoryServiceClient {

    @GetMapping("/api/inventory/{id}")
    Object getItemById(@PathVariable("id") Long id);

    @PutMapping("/api/inventory/{id}/deduct")
    void deductStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);
}