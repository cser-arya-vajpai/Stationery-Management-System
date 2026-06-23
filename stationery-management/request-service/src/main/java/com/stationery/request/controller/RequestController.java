package com.stationery.request.controller;

import com.stationery.request.dto.RequestResponseDto;
import com.stationery.request.dto.RequestStatusUpdateDto;
import com.stationery.request.dto.RequestSubmitDto;
import com.stationery.request.model.RequestStatus;
import com.stationery.request.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  //tells spring boot that this class is a controller
@RequestMapping("/api/requests")   //defines base URL path for all endpoints in this class. 
@RequiredArgsConstructor //Automatically injects RequestService interface via constructor injection
@CrossOrigin(origins = "*")  //Enables CORS. Allows react frontend to safely communicate with this microservice without browser blocking the request.
public class RequestController {

    private final RequestService requestService;   //request service is injected 

    @PostMapping   //Spring annotation that handles HTTP POST REQUESTS
    //@RequestBody instructs spring to read JSON payload from the incoming HTTP request and deserialize it into our RequestSubmitDto
    public ResponseEntity<RequestResponseDto> submitRequest(
            @Valid @RequestBody RequestSubmitDto dto,
            Authentication authentication) {  //extracts securely verified student email from the JWT bearer token
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(requestService.submitRequest(dto, authentication.getName()));
    }

    @GetMapping("/my")  //appends /my to the base path
    public ResponseEntity<List<RequestResponseDto>> getMyRequests(
            Authentication authentication,  //email extraction from JWT
            @RequestParam(required = false) RequestStatus status) {
        if (status != null) {
            return ResponseEntity.ok(
                    requestService.getMyRequestsByStatus(authentication.getName(), status));
        }
        return ResponseEntity.ok(requestService.getMyRequests(authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<RequestResponseDto>> getAllRequests(
            @RequestParam(required = false) RequestStatus status) {
        if (status != null) {
            return ResponseEntity.ok(requestService.getRequestsByStatus(status));
        }
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestResponseDto> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.getRequestById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RequestResponseDto> updateRequestStatus(
            @PathVariable Long id,
            @Valid @RequestBody RequestStatusUpdateDto dto) {
        return ResponseEntity.ok(requestService.updateRequestStatus(id, dto));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Request Service is running");
    }
}