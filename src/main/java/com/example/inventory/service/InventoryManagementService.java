package com.example.inventory.service;

import com.example.inventory.ItemLocationUpdate;
import com.example.inventory.model.ItemLocation;
import com.example.inventory.model.LocationHistory;
import com.example.inventory.repository.ItemLocationRepository;
import com.example.inventory.repository.LocationHistoryRepository;
import com.example.receive.model.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryManagementService {

    private final ItemLocationRepository itemLocationRepository;

    private final LocationHistoryRepository locationHistoryRepository;

    @Transactional
    public List<String> updateItemLocations(String receiptKey, List<ItemLocationUpdate> items,
                                          String warehouseId, String updatedBy) {
        List<String> updatedItems = new ArrayList<>();

        for (ItemLocationUpdate item : items) {
            ItemLocation itemLocation = new ItemLocation();
            itemLocation.setReceiptKey(receiptKey);
            itemLocation.setItemCode(item.getItemCode());
            itemLocation.setItemName(item.getItemName());
            itemLocation.setQuantity(item.getQuantity());
            itemLocation.setWarehouseId(warehouseId);
            itemLocation.setZone(item.getZone());
            itemLocation.setBinLocation(item.getBinLocation());
            itemLocation.setBatchNumber(item.getBatchNumber());
            itemLocation.setExpiryDate(item.getExpiryDate());
            itemLocation.setPlacedDate(LocalDateTime.now());
            itemLocation.setLastUpdated(LocalDateTime.now());
            itemLocation.setUpdatedBy(updatedBy);

            itemLocationRepository.save(itemLocation);
            updatedItems.add(item.getItemCode());
        }
        return updatedItems;
    }

    @Transactional
    public String updateItemLocations(Inventory item) {

        ItemLocation itemLocation = new ItemLocation();
        itemLocation.setReceiptKey(item.getReceiptKey());
        itemLocation.setItemCode(item.getItemCode());
        itemLocation.setItemName(item.getItemName());
        itemLocation.setQuantity(item.getQuantity());
        itemLocation.setWarehouseId(item.getWarehouseId());
        itemLocation.setZone(item.getZone());
        itemLocation.setBinLocation(item.getBinLocation());
        itemLocation.setBatchNumber(item.getBatchNumber());
        itemLocation.setExpiryDate(item.getExpiryDate());
        itemLocation.setPlacedDate(LocalDateTime.now());
        itemLocation.setLastUpdated(LocalDateTime.now());
        itemLocation.setUpdatedBy(item.getUpdatedBy());

        itemLocationRepository.save(itemLocation);
        return "Item location updated for item: " + item.getItemCode();
    }

    public List<ItemLocation> getItemLocationsByReceipt(String receiptKey) {
        return itemLocationRepository.findByReceiptKey(receiptKey);
    }

    @Transactional
    public boolean moveItemToNewLocation(String receiptKey, String itemCode,
                                       String newZone, String newBinLocation, String updatedBy) {
        Optional<ItemLocation> itemLocationOpt =
            itemLocationRepository.findByReceiptKeyAndItemCode(receiptKey, itemCode);

        if (itemLocationOpt.isPresent()) {
            ItemLocation itemLocation = itemLocationOpt.get();

            // Create history record
            LocationHistory history = new LocationHistory();
            history.setReceiptKey(receiptKey);
            history.setItemCode(itemCode);
            history.setOldZone(itemLocation.getZone());
            history.setOldBinLocation(itemLocation.getBinLocation());
            history.setNewZone(newZone);
            history.setNewBinLocation(newBinLocation);
            history.setUpdatedBy(updatedBy);
            history.setUpdatedDate(LocalDateTime.now());

            locationHistoryRepository.save(history);

            // Update item location
            itemLocation.setZone(newZone);
            itemLocation.setBinLocation(newBinLocation);
            itemLocation.setLastUpdated(LocalDateTime.now());
            itemLocation.setUpdatedBy(updatedBy);

            itemLocationRepository.save(itemLocation);
            return true;
        }
        return false;
    }

    public List<ItemLocation> getAllInventory() {
        return itemLocationRepository.findAll();
    }

    public List<?> getLocationHistoryByReceipt(String receiptKey) {
        return locationHistoryRepository.findByReceiptKey(receiptKey);
    }

    public List<LocationHistory> getLocationHistoryByItem(String receiptKey, String itemCode) {
        return locationHistoryRepository.findByReceiptKeyAndItemCodeOrderByUpdatedDateDesc(receiptKey, itemCode);
    }
}