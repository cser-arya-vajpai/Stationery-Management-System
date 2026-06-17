package com.stationery.inventory.dto;

import com.stationery.inventory.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationeryItemResponse {
    private Long id;
    private String name;
    private Category category;
    private String unit;
    private Integer availableQuantity;
    private Integer minimumQuantity;
    private boolean lowStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String lastUpdatedBy;
}