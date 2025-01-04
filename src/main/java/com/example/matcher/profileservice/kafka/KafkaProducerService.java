package com.example.matcher.profileservice.kafka;

import com.example.matcher.profileservice.dto.kafkaEvent.ProfileEvent;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> stringKafkaTemplate;

    private final KafkaTemplate<String, ProfileEvent> profileEventKafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    public void sendMessage(ProfileEvent profileEvent, String topicName) {
        logger.info("[KAFKA] SEND MESSAGE: " + profileEvent);
        profileEventKafkaTemplate.send(topicName, profileEvent);
    }

    public void sendMessage(String userId, String topicName) {
        stringKafkaTemplate.send(topicName, userId);
    }
}


