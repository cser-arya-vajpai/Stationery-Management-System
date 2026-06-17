package com.stationery.request.service;

import com.stationery.request.dto.RequestResponseDto;
import com.stationery.request.dto.RequestStatusUpdateDto;
import com.stationery.request.dto.RequestSubmitDto;
import com.stationery.request.model.RequestStatus;

import java.util.List;

public interface RequestService {
    RequestResponseDto submitRequest(RequestSubmitDto dto, String studentEmail);
    List<RequestResponseDto> getMyRequests(String studentEmail);
    List<RequestResponseDto> getMyRequestsByStatus(String studentEmail, RequestStatus status);
    List<RequestResponseDto> getAllRequests();
    List<RequestResponseDto> getRequestsByStatus(RequestStatus status);
    RequestResponseDto updateRequestStatus(Long id, RequestStatusUpdateDto dto);
    RequestResponseDto getRequestById(Long id);
}