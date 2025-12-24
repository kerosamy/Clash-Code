package com.clashcode.backend.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final long ONLINE_TTL_SECONDS = 60;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addUserToRedis(Long userId) {
        String key = getKey(userId);
        redisTemplate.opsForValue()
                .set(key, "1", Duration.ofSeconds(ONLINE_TTL_SECONDS));
    }

    public Boolean searchUserFromRedis(Long userId) {
        String key = getKey(userId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));

    }

    private String getKey(Long userId) {
        return "online:user:" + userId;
    }

}
