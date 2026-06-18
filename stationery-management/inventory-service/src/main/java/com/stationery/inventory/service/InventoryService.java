package com.stationery.inventory.service;

import com.stationery.inventory.dto.StationeryItemRequest;
import com.stationery.inventory.dto.StationeryItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryService {
    StationeryItemResponse addItem(StationeryItemRequest request, String adminEmail);
    StationeryItemResponse updateItem(Long id, StationeryItemRequest request, String adminEmail);
    StationeryItemResponse getItemById(Long id);
    Page<StationeryItemResponse> getAllItems(Pageable pageable);
    List<StationeryItemResponse> getLowStockItems();
    void deleteItem(Long id);
    void deductStock(Long id, int quantity); // Add this method
}