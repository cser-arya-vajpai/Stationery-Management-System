package com.stationery.request.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

//whenever a student submits a new stationery request, this DTO is used.
@Data
public class RequestSubmitDto {

    @NotEmpty(message = "Request must contain at least one item")
    private List<@Valid RequestItemDto> items;

    private String remarks;
}