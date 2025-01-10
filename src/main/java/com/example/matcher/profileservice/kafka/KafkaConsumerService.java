package com.example.matcher.profileservice.kafka;


import com.example.matcher.profileservice.model.StatusConnectionUpdate;
import com.example.matcher.profileservice.service.ProfileService;
import com.example.matcher.profileservice.service.StatusConnectionService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaConsumerService {
//    private final StudentRepository studentRepository;
    private final StatusConnectionService statusConnectionService;
    @KafkaListener(topics = "status_connection_update", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "jsonKafkaListenerContainerFactory")
    public void listenStatusConnectionUpdate(StatusConnectionUpdate newStatus) {
        statusConnectionService.statusConnectionUpdate(newStatus);
        System.out.println("[KAFKA]--[TOPIC: status_connection_update] Received msg: " + newStatus);
//        elasticsearchService.deleteItemByUUID(userId);
    }

}
