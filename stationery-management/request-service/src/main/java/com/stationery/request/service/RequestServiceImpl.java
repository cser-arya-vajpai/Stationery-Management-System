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

@Service    //tells Spring boot that this is a Service bean. Spring registers it in the application context.
@RequiredArgsConstructor   //it will automatically generate a constructor containing all final fields. Spring uses this constructor to inject StationeryRequestRepository and InventoryServiceClient
public class RequestServiceImpl implements RequestService {

    private final StationeryRequestRepository requestRepository;
    private final InventoryServiceClient inventoryServiceClient; // Inject the Feign client

    //submitting a request:
    //It receives the dto and the student's email(extracted from JWT)
    //Constructs a new DB StationeryRequest entity using Lombok's builder.
    //Saves the entity to the DB via requestRepository.save()
    //converts the saved databases record into a clean RequestResponseDto using the helper method mapToResponse and returns it.
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


    //Retrieving student requests 
    //calls the repo to query all requests under this given student email
    //Uses Java Streams (.stream()) to convert the list of DB entities into ResponseDTO 
    //collects it back into a Java List and returns it
    @Override
    public List<RequestResponseDto> getMyRequests(String studentEmail) {
        return requestRepository.findByStudentEmail(studentEmail)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //Filter my requests (by status)
    //Calls the repo method findByStudentEmailAdndStatus.
    //converts resulting DB entities into a Response DTO using Java Streams
    @Override
    public List<RequestResponseDto> getMyRequestsByStatus(String studentEmail,
                                                           RequestStatus status) {
        return requestRepository.findByStudentEmailAndStatus(studentEmail, status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());  //to pack the processed stream elements back into a std Java List.
    }

    //admin view- get all requests
    //invokes standard JPA method .findAll() to retrieve all rows from the stationery_requests table
    //Using Java streams, converts into ResponseDTO
    @Override
    public List<RequestResponseDto> getAllRequests() {
        return requestRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //admin filter all requests
    @Override
    public List<RequestResponseDto> getRequestsByStatus(RequestStatus status) {
        return requestRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //Searches the DB for the request using. If not found -> error
    //If for that request, new status = approved and current status != approved, then it triggers a call to inventory-service to deduct stock
    //Updates the status
    //Saves the updated request back to DB and returns a response
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
        if (dto.getRejectionReason() != null) {   //means the request is rejected
            request.setRejectionReason(dto.getRejectionReason());
        }

        return mapToResponse(requestRepository.save(request));
    }

    //admin side filter
    @Override
    public RequestResponseDto getRequestById(Long id) {
        StationeryRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RequestNotFoundException(
                        "Request not found with id: " + id));
        return mapToResponse(request);
    }

    //private helper method.
    //converts a db entity to a clean outgoing DTO
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