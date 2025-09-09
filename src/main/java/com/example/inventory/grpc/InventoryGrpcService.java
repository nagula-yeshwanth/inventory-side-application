package com.example.inventory.grpc;

import com.example.inventory.*;
import com.example.inventory.service.InventoryManagementService;
import com.example.inventory.model.ItemLocation;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
//import org.springframework.grpc.server.service.GrpcService;
import net.devh.boot.grpc.server.service.GrpcService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final InventoryManagementService inventoryService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void updateItemLocation(ItemLocationUpdateRequest request,
                                 StreamObserver<ItemLocationUpdateResponse> responseObserver) {
        try {
            List<String> updatedItems = inventoryService.updateItemLocations(
                request.getReceiptKey(),
                request.getItemsList(),
                request.getWarehouseId(),
                request.getUpdatedBy()
            );
 
            ItemLocationUpdateResponse response = ItemLocationUpdateResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Successfully updated " + updatedItems.size() + " items")
                .addAllUpdatedItems(updatedItems)
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            ItemLocationUpdateResponse response = ItemLocationUpdateResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Failed to update items: " + e.getMessage())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getItemLocationsByReceipt(LocationQueryRequest request,
                                        StreamObserver<LocationQueryResponse> responseObserver) {
        try {
            List<ItemLocation> items = inventoryService.getItemLocationsByReceipt(request.getReceiptKey());

            List<ItemLocationInfo> itemInfos = items.stream()
                .map(this::convertToItemLocationInfo)
                .collect(Collectors.toList());

            LocationQueryResponse response = LocationQueryResponse.newBuilder()
                .setFound(!items.isEmpty())
                .setReceiptKey(request.getReceiptKey())
                .addAllItems(itemInfos)
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            LocationQueryResponse response = LocationQueryResponse.newBuilder()
                .setFound(false)
                .setReceiptKey(request.getReceiptKey())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void moveItemToNewLocation(ItemLocationMoveRequest request,
                                    StreamObserver<ItemLocationMoveResponse> responseObserver) {
        try {
            boolean success = inventoryService.moveItemToNewLocation(
                request.getReceiptKey(),
                request.getItemCode(),
                request.getNewZone(),
                request.getNewBinLocation(),
                request.getUpdatedBy()
            );

            ItemLocationMoveResponse response = ItemLocationMoveResponse.newBuilder()
                .setSuccess(success)
                .setMessage(success ? "Item moved successfully" : "Item not found")
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            ItemLocationMoveResponse response = ItemLocationMoveResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Failed to move item: " + e.getMessage())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    private ItemLocationInfo convertToItemLocationInfo(ItemLocation item) {
        return ItemLocationInfo.newBuilder()
            .setItemCode(item.getItemCode())
            .setItemName(item.getItemName() != null ? item.getItemName() : "")
            .setQuantity(item.getQuantity() != null ? item.getQuantity() : 0)
            .setZone(item.getZone() != null ? item.getZone() : "")
            .setBinLocation(item.getBinLocation() != null ? item.getBinLocation() : "")
            .setBatchNumber(item.getBatchNumber() != null ? item.getBatchNumber() : "")
            .setExpiryDate(item.getExpiryDate() != null ? item.getExpiryDate() : "")
            .setPlacedDate(item.getPlacedDate() != null ? item.getPlacedDate().format(FORMATTER) : "")
            .build();
    }
}