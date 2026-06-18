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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
                .lastUpdatedBy("admin@test.com") // Added lastUpdatedBy here
                .build();
    }

    @Test
    void addItem_ShouldReturnResponse_WhenValidRequest() {
        when(itemRepository.save(any(StationeryItem.class))).thenReturn(stationeryItem);

        StationeryItemResponse response = inventoryService.addItem(itemRequest, "admin@test.com");

        assertNotNull(response);
        assertEquals("A4 Paper", response.getName());
        assertEquals(Category.PAPER, response.getCategory());
        assertEquals("admin@test.com", response.getLastUpdatedBy());
    }

    @Test
    void updateItem_ShouldReturnResponse_WhenValidRequest() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(stationeryItem));
        when(itemRepository.save(any(StationeryItem.class))).thenReturn(stationeryItem);

        StationeryItemResponse response = inventoryService.updateItem(1L, itemRequest, "admin@test.com");

        assertNotNull(response);
        assertEquals("admin@test.com", response.getLastUpdatedBy());
    }

    @Test
    void updateItem_ShouldThrowException_WhenItemNotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> inventoryService.updateItem(99L, itemRequest, "admin@test.com"));
    }

    @Test
    void getItemById_ShouldReturnResponse_WhenItemFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(stationeryItem));

        StationeryItemResponse response = inventoryService.getItemById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getItemById_ShouldThrowException_WhenItemNotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> inventoryService.getItemById(99L));
    }

    @Test
    void getAllItems_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<StationeryItem> page = new PageImpl<>(Collections.singletonList(stationeryItem));
        when(itemRepository.findAll(pageable)).thenReturn(page);

        Page<StationeryItemResponse> responses = inventoryService.getAllItems(pageable);

        assertNotNull(responses);
        assertEquals(1, responses.getTotalElements());
    }

    @Test
    void getLowStockItems_ShouldReturnFilteredList() {
        StationeryItem lowStockItem = StationeryItem.builder()
                .id(2L)
                .name("Low Pen")
                .availableQuantity(5)
                .minimumQuantity(10)
                .build();
        StationeryItem normalItem = StationeryItem.builder()
                .id(3L)
                .name("High Pen")
                .availableQuantity(50)
                .minimumQuantity(10)
                .build();
        when(itemRepository.findAll()).thenReturn(Arrays.asList(lowStockItem, normalItem));

        List<StationeryItemResponse> lowStockList = inventoryService.getLowStockItems();

        assertNotNull(lowStockList);
        assertEquals(1, lowStockList.size());
        assertEquals("Low Pen", lowStockList.get(0).getName());
    }

    @Test
    void deleteItem_ShouldSucceed_WhenItemExists() {
        when(itemRepository.existsById(1L)).thenReturn(true);
        doNothing().when(itemRepository).deleteById(1L);

        assertDoesNotThrow(() -> inventoryService.deleteItem(1L));
        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteItem_ShouldThrowException_WhenItemNotFound() {
        when(itemRepository.existsById(99L)).thenReturn(false);

        assertThrows(ItemNotFoundException.class,
                () -> inventoryService.deleteItem(99L));
    }

    @Test
    void deductStock_ShouldSucceed_WhenSufficientStock() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(stationeryItem));
        when(itemRepository.save(any(StationeryItem.class))).thenReturn(stationeryItem);

        assertDoesNotThrow(() -> inventoryService.deductStock(1L, 10));
        assertEquals(90, stationeryItem.getAvailableQuantity());
    }

    @Test
    void deductStock_ShouldThrowException_WhenInsufficientStock() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(stationeryItem));

        assertThrows(IllegalArgumentException.class,
                () -> inventoryService.deductStock(1L, 150));
    }

    @Test
    void deductStock_ShouldThrowException_WhenItemNotFound() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class,
                () -> inventoryService.deductStock(99L, 10));
    }
}