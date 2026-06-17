package com.stationery.inventory.service;

import com.stationery.inventory.dto.StationeryItemRequest;
import com.stationery.inventory.dto.StationeryItemResponse;
import com.stationery.inventory.exception.ItemNotFoundException;
import com.stationery.inventory.model.Category;
import com.stationery.inventory.model.StationeryItem;
import com.stationery.inventory.repository.StationeryItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private StationeryItemRepository itemRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private StationeryItemRequest itemRequest;
    private StationeryItem stationeryItem;

    @BeforeEach
    void setUp() {
        itemRequest = new StationeryItemRequest();
        itemRequest.setName("A4 Paper");
        itemRequest.setCategory(Category.PAPER);
        itemRequest.setUnit("Ream");
        itemRequest.setAvailableQuantity(100);
        itemRequest.setMinimumQuantity(20);

        stationeryItem = StationeryItem.builder()
                .id(1L)
                .name("A4 Paper")
                .category(Category.PAPER)
                .unit("Ream")
                .availableQuantity(100)
                .minimumQuantity(20)
                .build();
    }

    @Test
    void addItem_ShouldReturnResponse_WhenValidRequest() {
        when(itemRepository.save(any(StationeryItem.class))).thenReturn(stationeryItem);

        StationeryItemResponse response = inventoryService.addItem(itemRequest, "admin@test.com");

        assertNotNull(response);
        assertEquals("A4 Paper", response.getName());
        assertEquals(Category.PAPER, response.getCategory());
    }

    @Test
    void getItemById_ShouldThrowException_WhenItemNotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> inventoryService.getItemById(99L));
    }
}