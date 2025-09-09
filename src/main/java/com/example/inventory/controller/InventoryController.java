package com.example.inventory.controller;


import com.example.inventory.dto.MoveItemRequest;
import com.example.inventory.model.ItemLocation;
import com.example.inventory.model.LocationHistory;
import com.example.inventory.service.InventoryManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryManagementService inventoryService;

    @GetMapping("/")
    public ResponseEntity<?> getAllInventory() {

        List<ItemLocation> allInventory = inventoryService.getAllInventory();
        if (allInventory != null && !allInventory.isEmpty()) {
            return new ResponseEntity<>(allInventory, HttpStatus.OK);
        }
        return new ResponseEntity<>("No inventory found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{receiptKey}")
    public ResponseEntity<?> getInventoryByReceipt(@PathVariable String receiptKey) {
        List<ItemLocation> inventoryList = inventoryService.getItemLocationsByReceipt(receiptKey);
        if (inventoryList != null && !inventoryList.isEmpty()) {
            return new ResponseEntity<>(inventoryList, HttpStatus.OK);
        }
        return new ResponseEntity<>("No inventory found for the given receiptKey", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/locationHistory/{receiptKey}")
    public ResponseEntity<?> getLocationHistoryByReceipt(@PathVariable String receiptKey) {
        List<?> locationHistoryList = inventoryService.getLocationHistoryByReceipt(receiptKey);
        if (locationHistoryList != null && !locationHistoryList.isEmpty()) {
            return new ResponseEntity<>(locationHistoryList, HttpStatus.OK);
        }
        return new ResponseEntity<>("No location history found for the given receiptKey", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/locationHistory/{receiptKey}/{itemCode}")
    public ResponseEntity<?> getLocationHistoryByItem(@PathVariable String receiptKey, @PathVariable String itemCode) {
        List<LocationHistory> locationHistoryList = inventoryService.getLocationHistoryByItem(receiptKey, itemCode);
        if (locationHistoryList != null && !locationHistoryList.isEmpty()) {
            return new ResponseEntity<>(locationHistoryList, HttpStatus.OK);
        }
        return new ResponseEntity<>("No location history found for the given receiptKey and itemCode", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/moveItem")
    public ResponseEntity<?> moveItem(@RequestBody MoveItemRequest moveItemRequest) {
        boolean success = inventoryService.moveItemToNewLocation(
            moveItemRequest.getReceiptKey(),
            moveItemRequest.getItemCode(),
            moveItemRequest.getNewZone(),
            moveItemRequest.getNewBinLocation(),
            moveItemRequest.getUpdatedBy()
        );
        if (success) {
            return new ResponseEntity<>("Item moved successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Item not found or move failed", HttpStatus.NOT_FOUND);
    }
    
    
}
