package com.stationery.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stationery.request.dto.RequestResponseDto;
import com.stationery.request.dto.RequestStatusUpdateDto;
import com.stationery.request.dto.RequestSubmitDto;
import com.stationery.request.model.RequestStatus;
import com.stationery.request.service.RequestService;
import com.stationery.request.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest(RequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private RequestSubmitDto submitDto;
    private RequestResponseDto responseDto;
    private Authentication mockAuthentication;

    @BeforeEach
    void setUp() {
        submitDto = new RequestSubmitDto();
        submitDto.setItemId(1L);
        submitDto.setItemName("Pens");
        submitDto.setRequestedQuantity(10);

        responseDto = RequestResponseDto.builder()
                .id(1L)
                .requestId("REQ-99")
                .studentEmail("student@test.com")
                .itemId(1L)
                .itemName("Pens")
                .requestedQuantity(10)
                .status(RequestStatus.PENDING)
                .build();

        mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn("student@test.com");
    }

    @Test
    void submitRequest_ShouldReturnCreated() throws Exception {
        when(requestService.submitRequest(any(RequestSubmitDto.class), eq("student@test.com"))).thenReturn(responseDto);

        mockMvc.perform(post("/api/requests")
                .principal(mockAuthentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submitDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.requestId").value("REQ-99"));
    }

    @Test
    void getMyRequests_ShouldReturnList() throws Exception {
        when(requestService.getMyRequests("student@test.com")).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/requests/my")
                .principal(mockAuthentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value("REQ-99"));
    }

    @Test
    void getAllRequests_ShouldReturnList() throws Exception {
        when(requestService.getAllRequests()).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(get("/api/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requestId").value("REQ-99"));
    }

    @Test
    void updateRequestStatus_ShouldReturnOk() throws Exception {
        RequestStatusUpdateDto updateDto = new RequestStatusUpdateDto();
        updateDto.setStatus(RequestStatus.APPROVED);
        when(requestService.updateRequestStatus(eq(1L), any(RequestStatusUpdateDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/requests/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }
}