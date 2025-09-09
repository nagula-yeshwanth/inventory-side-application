// LocationHistory.java
package com.example.inventory.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "location_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receiptKey;
    private String itemCode;
    private String oldZone;
    private String oldBinLocation;
    private String newZone;
    private String newBinLocation;
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}