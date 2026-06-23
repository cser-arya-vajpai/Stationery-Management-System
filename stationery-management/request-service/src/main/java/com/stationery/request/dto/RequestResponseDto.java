package com.stationery.request.dto;

import com.stationery.request.model.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//This DTO is what the microservice returns back to the frontend after any operation. 
//It includes all the request details, timestamps, and database IDs.

@Data
@Builder   //allows the service layer to easily map a db  entity to this response DTO in one line.
@NoArgsConstructor
@AllArgsConstructor
public class RequestResponseDto {
    private Long id;
    private String requestId;
    private String studentEmail;
    private Long itemId;
    private String itemName;
    private Integer requestedQuantity;
    private RequestStatus status;
    private String rejectionReason;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}