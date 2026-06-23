package com.stationery.request.service;

import com.stationery.request.dto.RequestResponseDto;
import com.stationery.request.dto.RequestStatusUpdateDto;
import com.stationery.request.dto.RequestSubmitDto;
import com.stationery.request.model.RequestStatus;

import java.util.List;


//service package has been split into two packages, interface and its implementation
//this is because in spring boot, it is best practice to define a service in two files.

//this interface will declare what action are available, like a contract.
//it outlines all operations that request microservice can perform 
public interface RequestService {
    RequestResponseDto submitRequest(RequestSubmitDto dto, String studentEmail);
    List<RequestResponseDto> getMyRequests(String studentEmail);
    List<RequestResponseDto> getMyRequestsByStatus(String studentEmail, RequestStatus status);
    List<RequestResponseDto> getAllRequests();
    List<RequestResponseDto> getRequestsByStatus(RequestStatus status);
    RequestResponseDto updateRequestStatus(Long id, RequestStatusUpdateDto dto);
    RequestResponseDto getRequestById(Long id);
}