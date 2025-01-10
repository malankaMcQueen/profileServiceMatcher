package com.example.matcher.profileservice.service;

import com.example.matcher.profileservice.model.StatusConnection;
import com.example.matcher.profileservice.model.StatusConnectionUpdate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StatusConnectionService {

    private final RedisService redisService;
    public void statusConnectionUpdate(StatusConnectionUpdate newStatus) {
        if (StatusConnection.ONLINE.equals(newStatus.getStatusConnection())) {
            redisService.save("user_status:" + newStatus.getUserId().toString(), newStatus);
        }
        else {
            redisService.delete("user_status:" + newStatus.getUserId().toString());
        }
    }
}
