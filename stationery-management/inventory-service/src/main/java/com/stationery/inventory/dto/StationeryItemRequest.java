package com.stationery.inventory.dto;

import com.stationery.inventory.model.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StationeryItemRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Category is required")
    private Category category;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Available quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer availableQuantity;

    @NotNull(message = "Minimum quantity is required")
    @Min(value = 0, message = "Minimum quantity cannot be negative")
    private Integer minimumQuantity;
}