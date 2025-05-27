package com.caoguzelmas.lost_found.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParsedItemDataDTO {

    private String itemName;
    private Integer quantity;
    private String place;
    private String sourceFileInfo;
}
