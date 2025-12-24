package com.clashcode.matching_service.service;

import com.clashcode.matching_service.dto.UserMatchingDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String KEY_FOR_Z_SET = "matching:queue";

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void insertUser(Long userId, Integer rate){
        long now = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(
                KEY_FOR_Z_SET,
                getZSetMember(userId),
                rate
        );
        String key = getHashKey(userId);
        redisTemplate.opsForHash().put(key, "rate", String.valueOf(rate));
        redisTemplate.opsForHash().put(key, "joinedAt", String.valueOf(now));
    }

    public void removeUser(Long userId){
        redisTemplate.opsForZSet().remove(
                KEY_FOR_Z_SET,
                getZSetMember(userId)
        );
        String key = getHashKey(userId);
        redisTemplate.delete(key);
    }

    public Set<String> getAllUsers(){
        return redisTemplate.opsForZSet().range(KEY_FOR_Z_SET,0, -1);
    }

    public Boolean isUserExist(String userKey){
       return redisTemplate.opsForZSet().score(KEY_FOR_Z_SET, userKey) != null;
    }

    public Set<String>getUsersInRage(Integer minRate , Integer maxRate){
        return redisTemplate.opsForZSet()
                .rangeByScore(
                        KEY_FOR_Z_SET,
                        minRate,
                        maxRate
                );
    }

    public UserMatchingDto getUserData(Long userId) {
        String key = getHashKey(userId);

        String ratingStr = (String) redisTemplate.opsForHash().get(key, "rate");
        String joinedAtStr = (String) redisTemplate.opsForHash().get(key, "joinedAt");

        if (ratingStr == null || joinedAtStr == null)
            return null;

        int rating = Integer.parseInt(ratingStr);
        long startTime = Long.parseLong(joinedAtStr);

        return UserMatchingDto.builder()
                .userId(userId)
                .userRating(rating)
                .startTime(startTime)
                .build();
    }
    
    public String getZSetMember(Long userId) {
        return "user:" + userId;
    }

    public String getHashKey(Long userId) {
        return "matching:user:" + userId;
    }

    public Long getUserIdFromString(String str){
        return Long.parseLong(str.replace("user:", ""));
    }

}
