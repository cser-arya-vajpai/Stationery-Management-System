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

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final StationeryItemRepository itemRepository;

    @Override
    public StationeryItemResponse addItem(StationeryItemRequest request, String adminEmail) {
        StationeryItem item = StationeryItem.builder()
                .name(request.getName())
                .category(request.getCategory())
                .unit(request.getUnit())
                .availableQuantity(request.getAvailableQuantity())
                .minimumQuantity(request.getMinimumQuantity())
                .lastUpdatedBy(adminEmail)
                .build();

        return mapToResponse(itemRepository.save(item));
    }

    @Override
    public StationeryItemResponse updateItem(Long id, StationeryItemRequest request,
                                              String adminEmail) {
        StationeryItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));

        item.setName(request.getName());
        item.setCategory(request.getCategory());
        item.setUnit(request.getUnit());
        item.setAvailableQuantity(request.getAvailableQuantity());
        item.setMinimumQuantity(request.getMinimumQuantity());
        item.setLastUpdatedBy(adminEmail);

        return mapToResponse(itemRepository.save(item));
    }

    @Override
    public StationeryItemResponse getItemById(Long id) {
        StationeryItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));
        return mapToResponse(item);
    }

    @Override
    public Page<StationeryItemResponse> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public List<StationeryItemResponse> getLowStockItems() {
        return itemRepository.findAll().stream()
                .filter(item -> item.getAvailableQuantity() < item.getMinimumQuantity())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }

        @Override
    public void deductStock(Long id, int quantity) {
        StationeryItem item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));

        if (item.getAvailableQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for item: " + item.getName());
        }

        item.setAvailableQuantity(item.getAvailableQuantity() - quantity);
        itemRepository.save(item);
    }

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