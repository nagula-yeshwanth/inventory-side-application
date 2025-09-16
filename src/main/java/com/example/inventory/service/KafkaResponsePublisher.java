package com.example.inventory.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaResponsePublisher {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage( String message) {
        kafkaTemplate.send("receive-topic", message);
    }
}
