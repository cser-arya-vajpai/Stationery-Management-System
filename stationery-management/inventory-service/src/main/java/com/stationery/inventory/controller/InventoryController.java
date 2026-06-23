package com.stationery.inventory.controller;

import com.stationery.inventory.dto.StationeryItemRequest;
import com.stationery.inventory.dto.StationeryItemResponse;
import com.stationery.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//front facing api layer of our microservice
@RestController   //this class is an api controller 
@RequestMapping("/api/inventory")   //any request like this will be directed here
@RequiredArgsConstructor
@CrossOrigin(origins = "*")    //disable CORS because we dont use cookies, so we dont need it 
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping      //maps post requests to /api/inventory
    //@Valid tells spring's validation engine to execute validation constraints like @NotBlank etc inside StationeryItemRequest
    //@RequestBody: Tells Spring to read the JSON payload from the HTTP request body and parse it into this Java request object.
    public ResponseEntity<StationeryItemResponse> addItem(
            @Valid @RequestBody StationeryItemRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.addItem(request, authentication.getName()));
    }

    @PutMapping("/{id}")   //maps put requests 
    public ResponseEntity<StationeryItemResponse> updateItem(
            @PathVariable Long id,    //extracts value of id from URL path
            @Valid @RequestBody StationeryItemRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                inventoryService.updateItem(id, request, authentication.getName()));
    }

    @GetMapping("/{id}")    //maps get requests 
    public ResponseEntity<StationeryItemResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getItemById(id));
    }

    @GetMapping
    public ResponseEntity<Page<StationeryItemResponse>> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(inventoryService.getAllItems(pageable));
    }  //Fetches a paginated catalog list of items.


    @GetMapping("/low-stock")
    public ResponseEntity<List<StationeryItemResponse>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }  //Fetches items below their minimum threshold limit.

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.noContent().build();
    } //removes an item from the catalog

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Inventory Service is running");
    }

    //called by request-service when the student order gets approved
    @PutMapping("/{id}/deduct")
    public ResponseEntity<Void> deductStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        inventoryService.deductStock(id, quantity);
        return ResponseEntity.ok().build();
    }
}