//package com.example.matcher.profileservice.kafka;
//
//import com.example.matcher.profileservice.dto.ProfileEvent;
//import lombok.AllArgsConstructor;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//@AllArgsConstructor
//public class KafkaProducerService {
//
//    private final KafkaTemplate<String, String> stringKafkaTemplate;
//    private final KafkaTemplate<String, ProfileEvent> profileEventKafkaTemplate;
//
//    public void sendMessage(ProfileEvent profileEvent, String topicName) {
//        profileEventKafkaTemplate.send(topicName, profileEvent);
//    }
//
//    public void sendMessage(String userId, String topicName) {
//        stringKafkaTemplate.send(topicName, userId);
//    }
//}
//
//
