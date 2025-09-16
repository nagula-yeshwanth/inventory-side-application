package com.example.inventory.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import com.example.receive.model.Inventory;

@Service
public class ConsumerService {

    @Autowired
    KafkaResponsePublisher kafkaResponsePublisher;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${topic.name}")
    private String topicName;

    @Autowired
    private InventoryManagementService inventoryManagementService;

    @KafkaListener(topicPartitions = {
            @TopicPartition(topic = "inventory-topic", partitions = { "1" })
            },
            groupId = "my-group"
    )
    public void listen(ConsumerRecord<String, Inventory> record) {
        Inventory inventory = record.value();
        System.out.println("Received inventory message: " + inventory);
        String res = inventoryManagementService.updateItemLocations(inventory);

        kafkaResponsePublisher.sendMessage("Successfully updated inventory");

        if(res != null) {
            System.out.println("Inventory updated successfully: " + res);
        } else {
            System.out.println("Failed to update inventory for: " + inventory);
        }

    }
}

