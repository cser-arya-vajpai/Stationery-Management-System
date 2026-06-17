package com.stationery.request.dto;

import com.stationery.request.model.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
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