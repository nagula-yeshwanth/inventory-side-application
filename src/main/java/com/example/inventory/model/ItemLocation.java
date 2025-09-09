// ItemLocation.java
package com.example.inventory.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String receiptKey;

    @Column(nullable = false)
    private String itemCode;

    private String itemName;
    private Integer quantity;
    private String warehouseId;
    private String zone;
    private String binLocation;
    private String batchNumber;
    private String expiryDate;

    @Column(name = "placed_date")
    private LocalDateTime placedDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    private String updatedBy;
}