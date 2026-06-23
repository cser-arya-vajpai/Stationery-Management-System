package com.stationery.inventory.service;

import com.stationery.inventory.dto.StationeryItemRequest;
import com.stationery.inventory.dto.StationeryItemResponse;
import com.stationery.inventory.exception.ItemNotFoundException;
import com.stationery.inventory.model.StationeryItem;
import com.stationery.inventory.repository.StationeryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service   //service bean
@RequiredArgsConstructor   //automatically  writes a constructor for all fields marked as final.
public class InventoryServiceImpl implements InventoryService {

    private final StationeryItemRepository itemRepository;  //lombok generates constructor for it

    @Override   //this method implements the contract in InventoryService.java
    public StationeryItemResponse addItem(StationeryItemRequest request, String adminEmail) {
        StationeryItem item = StationeryItem.builder()
                .name(request.getName())
                .category(request.getCategory())
                .unit(request.getUnit())
                .availableQuantity(request.getAvailableQuantity())
                .minimumQuantity(request.getMinimumQuantity())
                .lastUpdatedBy(adminEmail)
                .build();    //using lombok's builder pattern to copy properties from StationeryItemRequest to create a new StationeryItem DB entity.

        return mapToResponse(itemRepository.save(item));
    }

    @Override
    public StationeryItemResponse updateItem(Long id, StationeryItemRequest request,
                                              String adminEmail) {
        StationeryItem item = itemRepository.findById(id)   //asks db to find item by id
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));   //otherwise throw error that the item is not found

                //overwriting values of existing db record in RAM with the new values, sent in the Request DTO
        item.setName(request.getName());
        item.setCategory(request.getCategory());   
        item.setUnit(request.getUnit());
        item.setAvailableQuantity(request.getAvailableQuantity());
        item.setMinimumQuantity(request.getMinimumQuantity());
        item.setLastUpdatedBy(adminEmail);

        return mapToResponse(itemRepository.save(item));      //saving updated entity back to db and returns mapped StationeryItemResponse
    }

    @Override
    public StationeryItemResponse getItemById(Long id) {
        StationeryItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));
        return mapToResponse(item);   //fetches a single item by id
    }

    @Override
    public Page<StationeryItemResponse> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable).map(this::mapToResponse);
    }       //receives a catalog page of items 

    @Override
    public List<StationeryItemResponse> getLowStockItems() {
        return itemRepository.findAll().stream()
                .filter(item -> item.getAvailableQuantity() < item.getMinimumQuantity())
                .map(this::mapToResponse)
                .collect(Collectors.toList());     //list all items where stock is low
    }

    @Override
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);      //if item exists, delete
    }

        @Override
    public void deductStock(Long id, int quantity) {
        StationeryItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));

        if (item.getAvailableQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for item: " + item.getName());
        }

        item.setAvailableQuantity(item.getAvailableQuantity() - quantity);
        itemRepository.save(item);        //deduct if approved
    }

    //converts db entry into Response DTO:
    private StationeryItemResponse mapToResponse(StationeryItem item) {
        return StationeryItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .category(item.getCategory())
                .unit(item.getUnit())
                .availableQuantity(item.getAvailableQuantity())
                .minimumQuantity(item.getMinimumQuantity())
                .lowStock(item.getAvailableQuantity() < item.getMinimumQuantity())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .lastUpdatedBy(item.getLastUpdatedBy())
                .build();
    }
}