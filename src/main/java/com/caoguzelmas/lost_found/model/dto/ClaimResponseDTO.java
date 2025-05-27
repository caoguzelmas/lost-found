package com.caoguzelmas.lost_found.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimResponseDTO {

    private Long claimId;
    private LostItemDTO lostItemDetails;
    private UserDTO userDetails;
    private Integer claimedQuantity;
}
