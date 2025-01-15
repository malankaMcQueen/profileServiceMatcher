package com.example.matcher.profileservice.service;

import com.example.matcher.profileservice.model.StatusConnection;
import com.example.matcher.profileservice.model.StatusConnectionUpdate;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RedisService {

    private RedisTemplate<String, Object> redisTemplate;

    public void save(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    // todo продумать логику если запись такая не найдена (по умолчанию ничего не произойдет)
    public void extendRecord(String key, Duration duration) {
        redisTemplate.expire(key, Duration.ofMinutes(5));
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public List<StatusConnectionUpdate> getAllOnlineUsers() {
        List<StatusConnectionUpdate> onlineUsers = new ArrayList<>();
        // Итеративно сканируем ключи
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            ScanOptions options = ScanOptions.scanOptions().match("user_status:*").build();
            connection.scan(options).forEachRemaining(key -> {
                String keyStr = new String(key);
                StatusConnectionUpdate userStatus = (StatusConnectionUpdate) redisTemplate.opsForValue().get(keyStr);
                if (userStatus != null && userStatus.getStatusConnection() == StatusConnection.ONLINE) {
                    onlineUsers.add(userStatus);
                }
            });
            return null;
        });

        return onlineUsers;
    }
}
