package com.stationery.request.service;

import com.stationery.request.client.InventoryServiceClient;
import com.stationery.request.dto.RequestResponseDto;
import com.stationery.request.dto.RequestStatusUpdateDto;
import com.stationery.request.dto.RequestSubmitDto;
import com.stationery.request.dto.RequestItemDto;
import com.stationery.request.model.RequestItem;
import com.stationery.request.exception.RequestNotFoundException;
import java.util.ArrayList;
import com.stationery.request.model.RequestStatus;
import com.stationery.request.model.StationeryRequest;
import com.stationery.request.repository.StationeryRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private StationeryRequestRepository requestRepository;

    @Mock
    private InventoryServiceClient inventoryServiceClient;

    @InjectMocks
    private RequestServiceImpl requestService;

    private RequestSubmitDto submitDto;
    private StationeryRequest stationeryRequest;

    @BeforeEach
    void setUp() {
        RequestItemDto itemDto = RequestItemDto.builder()
                .itemId(1L)
                .itemName("A4 Paper")
                .requestedQuantity(5)
                .build();

        submitDto = new RequestSubmitDto();
        submitDto.setItems(Collections.singletonList(itemDto));
        submitDto.setRemarks("Needed for assignment");

        stationeryRequest = StationeryRequest.builder()
                .id(1L)
                .requestId("REQ-12345")
                .studentEmail("john@test.com")
                .status(RequestStatus.PENDING)
                .build();

        RequestItem requestItem = RequestItem.builder()
                .id(1L)
                .request(stationeryRequest)
                .itemId(1L)
                .itemName("A4 Paper")
                .requestedQuantity(5)
                .build();

        stationeryRequest.setItems(new ArrayList<>(Collections.singletonList(requestItem)));
    }

    @Test
    void submitRequest_ShouldReturnResponse_WhenValidRequest() {
        when(requestRepository.save(any(StationeryRequest.class))).thenReturn(stationeryRequest);

        RequestResponseDto response = requestService.submitRequest(submitDto, "john@test.com");

        assertNotNull(response);
        assertEquals("john@test.com", response.getStudentEmail());
        assertEquals(RequestStatus.PENDING, response.getStatus());
    }

    @Test
    void getMyRequests_ShouldReturnList() {
        when(requestRepository.findByStudentEmail("john@test.com"))
                .thenReturn(Collections.singletonList(stationeryRequest));

        List<RequestResponseDto> response = requestService.getMyRequests("john@test.com");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getMyRequestsByStatus_ShouldReturnFilteredList() {
        when(requestRepository.findByStudentEmailAndStatus("john@test.com", RequestStatus.PENDING))
                .thenReturn(Collections.singletonList(stationeryRequest));

        List<RequestResponseDto> response = requestService.getMyRequestsByStatus("john@test.com", RequestStatus.PENDING);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getAllRequests_ShouldReturnAll() {
        when(requestRepository.findAll()).thenReturn(Collections.singletonList(stationeryRequest));

        List<RequestResponseDto> response = requestService.getAllRequests();

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getRequestsByStatus_ShouldReturnFilteredList() {
        when(requestRepository.findByStatus(RequestStatus.PENDING))
                .thenReturn(Collections.singletonList(stationeryRequest));

        List<RequestResponseDto> response = requestService.getRequestsByStatus(RequestStatus.PENDING);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void updateRequestStatus_ShouldApproveAndDeductStock() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(stationeryRequest));
        when(requestRepository.save(any(StationeryRequest.class))).thenReturn(stationeryRequest);
        doNothing().when(inventoryServiceClient).deductStock(1L, 5);

        RequestStatusUpdateDto updateDto = new RequestStatusUpdateDto();
        updateDto.setStatus(RequestStatus.APPROVED);

        RequestResponseDto response = requestService.updateRequestStatus(1L, updateDto);

        assertNotNull(response);
        verify(inventoryServiceClient, times(1)).deductStock(1L, 5);
    }

    @Test
    void updateRequestStatus_ShouldRejectAndSetReason() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(stationeryRequest));
        when(requestRepository.save(any(StationeryRequest.class))).thenReturn(stationeryRequest);

        RequestStatusUpdateDto updateDto = new RequestStatusUpdateDto();
        updateDto.setStatus(RequestStatus.REJECTED);
        updateDto.setRejectionReason("Not available");

        RequestResponseDto response = requestService.updateRequestStatus(1L, updateDto);

        assertNotNull(response);
        verify(inventoryServiceClient, never()).deductStock(anyLong(), anyInt());
    }

    @Test
    void updateRequestStatus_ShouldThrowException_WhenNotFound() {
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());
        RequestStatusUpdateDto updateDto = new RequestStatusUpdateDto();

        assertThrows(RequestNotFoundException.class,
                () -> requestService.updateRequestStatus(99L, updateDto));
    }

    @Test
    void getRequestById_ShouldReturnResponse_WhenFound() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(stationeryRequest));

        RequestResponseDto response = requestService.getRequestById(1L);

        assertNotNull(response);
        assertEquals("REQ-12345", response.getRequestId());
    }

    @Test
    void getRequestById_ShouldThrowException_WhenNotFound() {
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class,
                () -> requestService.getRequestById(99L));
    }
}