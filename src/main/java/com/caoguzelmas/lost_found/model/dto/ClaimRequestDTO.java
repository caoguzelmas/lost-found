package com.caoguzelmas.lost_found.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimRequestDTO {

    @NotNull(message = "User ID cannot be blank")
    private Long userId;

    @NotNull(message = "Item ID cannot be null")
    private Long itemLocationInventoryId;

    @NotNull(message = "Claimed quantity cannot be null")
    @Min(value = 1, message = "Claimed quantity must be at least 1")
    private Integer claimedQuantity;
}
