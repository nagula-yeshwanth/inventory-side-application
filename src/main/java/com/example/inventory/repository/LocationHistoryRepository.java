package com.example.inventory.repository;

import com.example.inventory.model.LocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LocationHistoryRepository extends JpaRepository<LocationHistory, Long> {
    List<LocationHistory> findByReceiptKeyAndItemCodeOrderByUpdatedDateDesc(String receiptKey, String itemCode);
    List<LocationHistory> findByReceiptKeyOrderByUpdatedDateDesc(String receiptKey);

    List<?> findByReceiptKey(String receiptKey);

}