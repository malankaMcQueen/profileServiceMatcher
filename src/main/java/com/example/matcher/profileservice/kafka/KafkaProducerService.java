package com.example.matcher.profileservice.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;


    public void sendMessage(String message, String topicName) {
        kafkaTemplate.send(topicName, message);
    }
}