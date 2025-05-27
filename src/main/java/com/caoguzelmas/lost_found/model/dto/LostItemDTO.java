package com.caoguzelmas.lost_found.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LostItemDTO {

    private Long itemLocationInventoryId;
    private String itemName;
    private String place;
    private Integer totalFoundQuantity;
}
