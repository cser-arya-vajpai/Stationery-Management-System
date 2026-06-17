package com.stationery.request.service;

import com.stationery.request.dto.RequestResponseDto;
import com.stationery.request.dto.RequestSubmitDto;
import com.stationery.request.exception.RequestNotFoundException;
import com.stationery.request.model.RequestStatus;
import com.stationery.request.model.StationeryRequest;
import com.stationery.request.repository.StationeryRequestRepository;
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
class RequestServiceTest {

    @Mock
    private StationeryRequestRepository requestRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    private RequestSubmitDto submitDto;
    private StationeryRequest stationeryRequest;

    @BeforeEach
    void setUp() {
        submitDto = new RequestSubmitDto();
        submitDto.setItemId(1L);
        submitDto.setItemName("A4 Paper");
        submitDto.setRequestedQuantity(5);
        submitDto.setRemarks("Needed for assignment");

        stationeryRequest = StationeryRequest.builder()
                .id(1L)
                .requestId("REQ-12345")
                .studentEmail("john@test.com")
                .itemId(1L)
                .itemName("A4 Paper")
                .requestedQuantity(5)
                .status(RequestStatus.PENDING)
                .build();
    }

    @Test
    void submitRequest_ShouldReturnResponse_WhenValidRequest() {
        when(requestRepository.save(any(StationeryRequest.class)))
                .thenReturn(stationeryRequest);

        RequestResponseDto response = requestService.submitRequest(
                submitDto, "john@test.com");

        assertNotNull(response);
        assertEquals("john@test.com", response.getStudentEmail());
        assertEquals("A4 Paper", response.getItemName());
        assertEquals(RequestStatus.PENDING, response.getStatus());
    }

    @Test
    void getRequestById_ShouldThrowException_WhenNotFound() {
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class,
                () -> requestService.getRequestById(99L));
    }
}