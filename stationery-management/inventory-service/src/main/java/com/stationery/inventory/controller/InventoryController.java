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

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<StationeryItemResponse> addItem(
            @Valid @RequestBody StationeryItemRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.addItem(request, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StationeryItemResponse> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody StationeryItemRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                inventoryService.updateItem(id, request, authentication.getName()));
    }

    @GetMapping("/{id}")
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
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<StationeryItemResponse>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Inventory Service is running");
    }

    @PutMapping("/{id}/deduct")
    public ResponseEntity<Void> deductStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        inventoryService.deductStock(id, quantity);
        return ResponseEntity.ok().build();
    }
}