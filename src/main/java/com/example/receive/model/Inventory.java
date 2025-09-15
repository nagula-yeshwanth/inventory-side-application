package com.example.receive.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class Inventory {

    private String receiptKey;
    private String warehouseId;
    private String itemCode;
    private String itemName;
    private Integer quantity;
    private String zone;
    private String newZone;
    private String binLocation;
    private String newBinLocation;
    private String batchNumber;
    private String expiryDate;
    private String updatedBy;


}