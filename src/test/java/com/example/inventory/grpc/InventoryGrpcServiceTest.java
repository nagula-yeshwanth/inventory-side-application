package com.example.inventory.grpc;

import com.example.inventory.*;
import com.example.inventory.model.ItemLocation;
import com.example.inventory.service.InventoryManagementService;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryGrpcServiceTest {

    @Mock
    private InventoryManagementService inventoryService;

    @InjectMocks
    private InventoryGrpcService inventoryGrpcService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateItemLocation_Success() {
        // Arrange
        ItemLocationUpdateRequest request = ItemLocationUpdateRequest.newBuilder()
                .setReceiptKey("R123")
                .addItems(ItemLocationUpdate.newBuilder()
                        .setItemCode("I001")
                        .setItemName("Item1")
                        .setQuantity(10)
                        .setZone("Z1")
                        .setBinLocation("B1")
                        .build())
                .setWarehouseId("W1")
                .setUpdatedBy("User1")
                .build();

        when(inventoryService.updateItemLocations(anyString(), anyList(), anyString(), anyString()))
                .thenReturn(Collections.singletonList("I001"));

        StreamRecorder<ItemLocationUpdateResponse> responseObserver = StreamRecorder.create();

        // Act
        inventoryGrpcService.updateItemLocation(request, responseObserver);

        // Assert
        List<ItemLocationUpdateResponse> responses = responseObserver.getValues();
        assertEquals(1, responses.size());
        ItemLocationUpdateResponse response = responses.get(0);
        assertTrue(response.getSuccess());
        assertEquals("Successfully updated 1 items", response.getMessage());
        assertEquals(1, response.getUpdatedItemsCount());
        assertEquals("I001", response.getUpdatedItems(0));

        verify(inventoryService, times(1))
                .updateItemLocations(eq("R123"), anyList(), eq("W1"), eq("User1"));
    }

    @Test
    void testGetItemLocationsByReceipt_Found() {
        // Arrange
        LocationQueryRequest request = LocationQueryRequest.newBuilder()
                .setReceiptKey("R123")
                .build();

        ItemLocation item = new ItemLocation();
        item.setItemCode("I001");
        item.setItemName("Item1");
        item.setQuantity(10);
        item.setZone("Z1");
        item.setBinLocation("B1");

        when(inventoryService.getItemLocationsByReceipt("R123"))
                .thenReturn(Collections.singletonList(item));

        StreamRecorder<LocationQueryResponse> responseObserver = StreamRecorder.create();

        // Act
        inventoryGrpcService.getItemLocationsByReceipt(request, responseObserver);

        // Assert
        List<LocationQueryResponse> responses = responseObserver.getValues();
        assertEquals(1, responses.size());
        LocationQueryResponse response = responses.get(0);
        assertTrue(response.getFound());
        assertEquals("R123", response.getReceiptKey());
        assertEquals(1, response.getItemsCount());
        assertEquals("I001", response.getItems(0).getItemCode());

        verify(inventoryService, times(1)).getItemLocationsByReceipt("R123");
    }

    @Test
    void testMoveItemToNewLocation_Success() {
        // Arrange
        ItemLocationMoveRequest request = ItemLocationMoveRequest.newBuilder()
                .setReceiptKey("R123")
                .setItemCode("I001")
                .setNewZone("Z2")
                .setNewBinLocation("B2")
                .setUpdatedBy("User1")
                .build();

        when(inventoryService.moveItemToNewLocation("R123", "I001", "Z2", "B2", "User1"))
                .thenReturn(true);

        StreamRecorder<ItemLocationMoveResponse> responseObserver = StreamRecorder.create();

        // Act
        inventoryGrpcService.moveItemToNewLocation(request, responseObserver);

        // Assert
        List<ItemLocationMoveResponse> responses = responseObserver.getValues();
        assertEquals(1, responses.size());
        ItemLocationMoveResponse response = responses.get(0);
        assertTrue(response.getSuccess());
        assertEquals("Item moved successfully", response.getMessage());

        verify(inventoryService, times(1))
                .moveItemToNewLocation("R123", "I001", "Z2", "B2", "User1");
    }

    @Test
    void testUpdateItemLocation_Failure() {
        // Arrange
        ItemLocationUpdateRequest request = ItemLocationUpdateRequest.newBuilder()
                .setReceiptKey("R123")
                .addItems(ItemLocationUpdate.newBuilder()
                        .setItemCode("I001")
                        .setItemName("Item1")
                        .setQuantity(10)
                        .build())
                .setWarehouseId("W1")
                .setUpdatedBy("User1")
                .build();

        when(inventoryService.updateItemLocations(anyString(), anyList(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Database error"));

        StreamRecorder<ItemLocationUpdateResponse> responseObserver = StreamRecorder.create();

        // Act
        inventoryGrpcService.updateItemLocation(request, responseObserver);

        // Assert
        List<ItemLocationUpdateResponse> responses = responseObserver.getValues();
        assertEquals(1, responses.size());
        ItemLocationUpdateResponse response = responses.get(0);
        assertFalse(response.getSuccess());
        assertTrue(response.getMessage().contains("Failed to update items"));
    }

    @Test
    void testGetItemLocationsByReceipt_NotFound() {
        // Arrange
        LocationQueryRequest request = LocationQueryRequest.newBuilder()
                .setReceiptKey("R999")
                .build();

        when(inventoryService.getItemLocationsByReceipt("R999"))
                .thenReturn(Collections.emptyList());

        StreamRecorder<LocationQueryResponse> responseObserver = StreamRecorder.create();

        // Act
        inventoryGrpcService.getItemLocationsByReceipt(request, responseObserver);

        // Assert
        List<LocationQueryResponse> responses = responseObserver.getValues();
        assertEquals(1, responses.size());
        LocationQueryResponse response = responses.get(0);
        assertFalse(response.getFound());
        assertEquals("R999", response.getReceiptKey());
        assertEquals(0, response.getItemsCount());
    }

    @Test
    void testGetItemLocationsByReceipt_Exception() {
        // Arrange
        LocationQueryRequest request = LocationQueryRequest.newBuilder()
                .setReceiptKey("R123")
                .build();

        when(inventoryService.getItemLocationsByReceipt("R123"))
                .thenThrow(new RuntimeException("Database error"));

        StreamRecorder<LocationQueryResponse> responseObserver = StreamRecorder.create();

        // Act
        inventoryGrpcService.getItemLocationsByReceipt(request, responseObserver);

        // Assert
        List<LocationQueryResponse> responses = responseObserver.getValues();
        assertEquals(1, responses.size());
        LocationQueryResponse response = responses.get(0);
        assertFalse(response.getFound());
        assertEquals("R123", response.getReceiptKey());
    }

    @Test
    void testMoveItemToNewLocation_ItemNotFound() {
        // Arrange
        ItemLocationMoveRequest request = ItemLocationMoveRequest.newBuilder()
                .setReceiptKey("R123")
                .setItemCode("I999")
                .setNewZone("Z2")
                .setNewBinLocation("B2")
                .setUpdatedBy("User1")
                .build();

        when(inventoryService.moveItemToNewLocation("R123", "I999", "Z2", "B2", "User1"))
                .thenReturn(false);

        StreamRecorder<ItemLocationMoveResponse> responseObserver = StreamRecorder.create();

        // Act
        inventoryGrpcService.moveItemToNewLocation(request, responseObserver);

        // Assert
        List<ItemLocationMoveResponse> responses = responseObserver.getValues();
        assertEquals(1, responses.size());
        ItemLocationMoveResponse response = responses.get(0);
        assertFalse(response.getSuccess());
        assertEquals("Item not found", response.getMessage());
    }

    @Test
    void testMoveItemToNewLocation_Exception() {
        // Arrange
        ItemLocationMoveRequest request = ItemLocationMoveRequest.newBuilder()
                .setReceiptKey("R123")
                .setItemCode("I001")
                .setNewZone("Z2")
                .setNewBinLocation("B2")
                .setUpdatedBy("User1")
                .build();

        when(inventoryService.moveItemToNewLocation("R123", "I001", "Z2", "B2", "User1"))
                .thenThrow(new RuntimeException("Database error"));

        StreamRecorder<ItemLocationMoveResponse> responseObserver = StreamRecorder.create();

        // Act
        inventoryGrpcService.moveItemToNewLocation(request, responseObserver);

        // Assert
        List<ItemLocationMoveResponse> responses = responseObserver.getValues();
        assertEquals(1, responses.size());
        ItemLocationMoveResponse response = responses.get(0);
        assertFalse(response.getSuccess());
        assertTrue(response.getMessage().contains("Failed to move item"));
    }
}