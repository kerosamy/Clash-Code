package com.clashcode.backend.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long ONLINE_TTL_SECONDS = 45;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addUserToRedis(Long userId, String status) {
        try {
            redisTemplate.opsForValue()
                    .set(getKey(userId), status, Duration.ofSeconds(ONLINE_TTL_SECONDS));
        } catch (Exception ignored) {
            // Redis failure should NOT crash business logic
        }
    }

    public Boolean searchUserFromRedis(Long userId) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(getKey(userId)));
        } catch (Exception ignored) {
            return false;
        }
    }

    public String getUserStatus(Long userId) {
        try {
            return redisTemplate.opsForValue().get(getKey(userId));
        } catch (Exception ignored) {
            return null;
        }
    }

    private String getKey(Long userId) {
        return "online:user:" + userId;
    }
}
