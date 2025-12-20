package com.clashcode.matching_service.service;

import com.clashcode.matching_service.dto.MatchingRequestDto;
import com.clashcode.matching_service.dto.UserMatchingDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MatchingService {

    private final RedisTemplate<String, String> redisTemplate;
    private final MatchingNotifier matchingNotifier;
    private static final String KEY_FOR_Z_SET = "matching:queue";
    private static final Double MS_TO_MINUTE = 60_000.0;
    private static final Integer FACTOR_OF_MINUTE = 5;
    private static final Integer RANGE_INCREASING_FACTOR = 100;


    public MatchingService(RedisTemplate<String, String> redisTemplate,
                           MatchingNotifier matchingNotifier
    ) {
        this.redisTemplate = redisTemplate;
        this.matchingNotifier = matchingNotifier;
    }

    public void addUserToMatchingService(MatchingRequestDto dto) {
        long now = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(
                KEY_FOR_Z_SET,
                getZSetMember(dto.getUserId()),
                dto.getUserRating()
        );
        String key = getHashKey(dto.getUserId());
        redisTemplate.opsForHash().put(key, "rate", String.valueOf(dto.getUserRating()));
        redisTemplate.opsForHash().put(key, "joinedAt", String.valueOf(now));
    }

    public void removeUserFromMatchingService(Long userId) {
        redisTemplate.opsForZSet().remove(
                KEY_FOR_Z_SET,
                getZSetMember(userId)
        );
        String key = getHashKey(userId);
        redisTemplate.delete(key);
    }

    public void searchForOpponent(Long userId) {
        long now = System.currentTimeMillis();

        UserMatchingDto user = getUserData(userId);
        if (user == null) return;

        double waitingMinutes = ((now - user.getStartTime()) / (MS_TO_MINUTE)) * FACTOR_OF_MINUTE;
        int range = (int) (Math.ceil(waitingMinutes) * RANGE_INCREASING_FACTOR);
        System.out.println(range);
        int rating = user.getUserRating();

        Set<String> candidates = redisTemplate.opsForZSet()
                .rangeByScore(
                        KEY_FOR_Z_SET,
                        rating - range,
                        rating + range
                );

        if (candidates == null || candidates.isEmpty()) return;

        for (String candidate : candidates) {
            Long otherUserId = getUserIdFromString(candidate);
            if (userId.equals(otherUserId)) continue;
            matchTwoUsers(userId, otherUserId);
            System.out.println("Matched " + userId + " with " + otherUserId);
            break;
        }
    }

    public void matchTwoUsers(Long userId , Long otherUserId) {
        removeUserFromMatchingService(userId);
        removeUserFromMatchingService(otherUserId);
        matchingNotifier.notifyMatch(otherUserId, userId);
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
