package com.example.inventory.dto;

import lombok.Data;

@Data
public class MoveItemRequest {
    private String receiptKey;
    private String itemCode;
    private String newZone;
    private String newBinLocation;
    private String updatedBy;
}
