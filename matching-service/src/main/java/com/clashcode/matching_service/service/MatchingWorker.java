package com.clashcode.matching_service.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MatchingWorker {

    private final RedisService redisService;
    private final MatchingService matchingService;

    MatchingWorker(RedisService redisService, MatchingService matchingService) {
        this.matchingService = matchingService;
        this.redisService = redisService;
    }

    @Scheduled(fixedDelay = 2000)
    public void runMatchingService() {
        Set<String> waitingUsers = redisService.getAllUsers();
        if (waitingUsers == null) return;

        for (String waitingUser : waitingUsers) {
            long userId = redisService.getUserIdFromString(waitingUser);
            if (redisService.getUserData(userId) == null) continue;

            Boolean isStillWaiting = redisService.isUserExist(waitingUser);
            if (!Boolean.TRUE.equals(isStillWaiting)) continue;

            matchingService.searchForOpponent(userId);
        }
    }
}
