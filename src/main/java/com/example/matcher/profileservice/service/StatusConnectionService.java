package com.example.matcher.profileservice.service;

import com.example.matcher.profileservice.model.StatusConnection;
import com.example.matcher.profileservice.model.StatusConnectionUpdate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@AllArgsConstructor
public class StatusConnectionService {

    private final RedisService redisService;
    public void statusConnectionUpdate(StatusConnectionUpdate newStatus) {
        if (StatusConnection.ONLINE.equals(newStatus.getStatusConnection())) {
            redisService.save("user_status:" + newStatus.getUserId().toString(), newStatus, Duration.ofMinutes(5));
        }
        else {
            redisService.delete("user_status:" + newStatus.getUserId().toString());
        }
    }

    public List<StatusConnectionUpdate> getAllOnlineUsers() {
        return redisService.getAllOnlineUsers();
    }
}
