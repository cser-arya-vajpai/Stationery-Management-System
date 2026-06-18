package com.stationery.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stationery.inventory.dto.StationeryItemRequest;
import com.stationery.inventory.dto.StationeryItemResponse;
import com.stationery.inventory.model.Category;
import com.stationery.inventory.service.InventoryService;
import com.stationery.inventory.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private StationeryItemRequest itemRequest;
    private StationeryItemResponse itemResponse;
    private Authentication mockAuthentication;

    @BeforeEach
    void setUp() {
        itemRequest = new StationeryItemRequest();
        itemRequest.setName("Notebook");
        itemRequest.setCategory(Category.NOTEBOOK);
        itemRequest.setUnit("Piece");
        itemRequest.setAvailableQuantity(50);
        itemRequest.setMinimumQuantity(10);

        itemResponse = StationeryItemResponse.builder()
                .id(1L)
                .name("Notebook")
                .category(Category.NOTEBOOK)
                .unit("Piece")
                .availableQuantity(50)
                .minimumQuantity(10)
                .lastUpdatedBy("admin@test.com")
                .build();

        mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn("admin@test.com");
    }

    @Test
    void getAllItems_ShouldReturnPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<StationeryItemResponse> page = new PageImpl<>(Collections.singletonList(itemResponse), pageable, 1);
        when(inventoryService.getAllItems(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Notebook"));
    }

    @Test
    void getItemById_ShouldReturnItem() throws Exception {
        when(inventoryService.getItemById(1L)).thenReturn(itemResponse);

        mockMvc.perform(get("/api/inventory/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Notebook"));
    }

    @Test
    void addItem_ShouldReturnCreated() throws Exception {
        when(inventoryService.addItem(any(StationeryItemRequest.class), eq("admin@test.com"))).thenReturn(itemResponse);

        mockMvc.perform(post("/api/inventory")
                .principal(mockAuthentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Notebook"));
    }

    @Test
    void updateItem_ShouldReturnOk() throws Exception {
        when(inventoryService.updateItem(eq(1L), any(StationeryItemRequest.class), eq("admin@test.com"))).thenReturn(itemResponse);

        mockMvc.perform(put("/api/inventory/1")
                .principal(mockAuthentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteItem_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/inventory/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deductStock_ShouldReturnOk() throws Exception {
        mockMvc.perform(put("/api/inventory/1/deduct?quantity=5"))
                .andExpect(status().isOk());
    }
}