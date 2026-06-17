package com.stationery.request.dto;

import com.stationery.request.model.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestStatusUpdateDto {

    @NotNull(message = "Status is required")
    private RequestStatus status;

    private String rejectionReason;
}