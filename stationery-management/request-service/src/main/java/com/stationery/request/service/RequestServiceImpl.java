package com.stationery.request.service;

import com.stationery.request.client.InventoryServiceClient;
import com.stationery.request.dto.RequestResponseDto;
import com.stationery.request.dto.RequestStatusUpdateDto;
import com.stationery.request.dto.RequestSubmitDto;
import com.stationery.request.exception.RequestNotFoundException;
import com.stationery.request.model.RequestStatus;
import com.stationery.request.model.StationeryRequest;
import com.stationery.request.repository.StationeryRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final StationeryRequestRepository requestRepository;
    private final InventoryServiceClient inventoryServiceClient; // Inject the Feign client

    @Override
    public RequestResponseDto submitRequest(RequestSubmitDto dto, String studentEmail) {
        StationeryRequest request = StationeryRequest.builder()
                .studentEmail(studentEmail)
                .itemId(dto.getItemId())
                .itemName(dto.getItemName())
                .requestedQuantity(dto.getRequestedQuantity())
                .remarks(dto.getRemarks())
                .build();

        return mapToResponse(requestRepository.save(request));
    }

    @Override
    public List<RequestResponseDto> getMyRequests(String studentEmail) {
        return requestRepository.findByStudentEmail(studentEmail)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestResponseDto> getMyRequestsByStatus(String studentEmail,
                                                           RequestStatus status) {
        return requestRepository.findByStudentEmailAndStatus(studentEmail, status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestResponseDto> getAllRequests() {
        return requestRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestResponseDto> getRequestsByStatus(RequestStatus status) {
        return requestRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RequestResponseDto updateRequestStatus(Long id, RequestStatusUpdateDto dto) {
        StationeryRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RequestNotFoundException(
                        "Request not found with id: " + id));

        // If transitioning to APPROVED, call inventory-service to deduct stock
        if (dto.getStatus() == RequestStatus.APPROVED && request.getStatus() != RequestStatus.APPROVED) {
            inventoryServiceClient.deductStock(request.getItemId(), request.getRequestedQuantity());
        }

        request.setStatus(dto.getStatus());
        if (dto.getRejectionReason() != null) {
            request.setRejectionReason(dto.getRejectionReason());
        }

        return mapToResponse(requestRepository.save(request));
    }

    @Override
    public RequestResponseDto getRequestById(Long id) {
        StationeryRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RequestNotFoundException(
                        "Request not found with id: " + id));
        return mapToResponse(request);
    }

    private RequestResponseDto mapToResponse(StationeryRequest request) {
        return RequestResponseDto.builder()
                .id(request.getId())
                .requestId(request.getRequestId())
                .studentEmail(request.getStudentEmail())
                .itemId(request.getItemId())
                .itemName(request.getItemName())
                .requestedQuantity(request.getRequestedQuantity())
                .status(request.getStatus())
                .rejectionReason(request.getRejectionReason())
                .remarks(request.getRemarks())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}