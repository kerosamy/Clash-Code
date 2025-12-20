package com.clashcode.matching_service.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MatchingWorker {

    private final RedisTemplate<String, String> redisTemplate;
    private final MatchingService matchingService;
    private static final String KEY_FOR_Z_SET = "matching:queue";


    MatchingWorker(RedisTemplate<String, String> redisTemplate, MatchingService matchingService) {
        this.redisTemplate = redisTemplate;
        this.matchingService = matchingService;
    }
    @Scheduled(fixedDelay = 2000)
    public void runMatchingService() {
        System.out.println("Running Matching Service");
        Set<String> waitingUsers = redisTemplate.opsForZSet().range(KEY_FOR_Z_SET,0, -1);
        if (waitingUsers == null) return;

        for (String waitingUser : waitingUsers) {
            long userId = matchingService.getUserIdFromString(waitingUser);
            if (matchingService.getUserData(userId) == null) continue;


            Boolean isStillWaiting = redisTemplate.opsForZSet().score(KEY_FOR_Z_SET, waitingUser) != null;
            if (!Boolean.TRUE.equals(isStillWaiting)) continue;

            System.out.println(userId);
            matchingService.searchForOpponent(userId);
        }
    }

}
