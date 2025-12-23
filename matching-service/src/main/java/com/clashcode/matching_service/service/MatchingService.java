package com.clashcode.matching_service.service;

import com.clashcode.matching_service.dto.MatchingRequestDto;
import com.clashcode.matching_service.dto.UserMatchingDto;
import com.clashcode.matching_service.main_backend.MainBackendClient;
import com.clashcode.matching_service.main_backend.dto.MatchCreationDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MatchingService {

    private final MainBackendClient mainBackendClient;
    private final RedisService redisService;
    private static final Double MS_TO_MINUTE = 60_000.0;
    private static final Integer FACTOR_OF_MINUTE = 5;
    private static final Integer RANGE_INCREASING_FACTOR = 100;


    public MatchingService(
            RedisService redisService,
            MainBackendClient mainBackendClient
    ) {
        this.redisService = redisService;
        this.mainBackendClient = mainBackendClient;
    }

    public void addUserToMatchingService(MatchingRequestDto dto) {
        redisService.insertUser(dto.getUserId(), dto.getUserRating());
    }

    public void removeUserFromMatchingService(Long userId) {
       redisService.removeUser(userId);
    }

    public void searchForOpponent(Long userId) {
        long now = System.currentTimeMillis();

        UserMatchingDto user = redisService.getUserData(userId);
        if (user == null) return;

        double waitingMinutes = ((now - user.getStartTime()) / (MS_TO_MINUTE)) * FACTOR_OF_MINUTE;
        int range = (int) (Math.ceil(waitingMinutes) * RANGE_INCREASING_FACTOR);
        int rating = user.getUserRating();

        Set<String> candidates = redisService.getUsersInRage(rating-range, rating+range);

        if (candidates == null || candidates.isEmpty()) return;

        for (String candidate : candidates) {
            Long otherUserId = redisService.getUserIdFromString(candidate);
            if (userId.equals(otherUserId)) continue;
            matchTwoUsers(userId, otherUserId);
            break;
        }
    }

    public void matchTwoUsers(Long userId , Long otherUserId) {
        removeUserFromMatchingService(userId);
        removeUserFromMatchingService(otherUserId);
        MatchCreationDto dto = new MatchCreationDto(userId , otherUserId);
        mainBackendClient.MatchingTwoPlayers(dto);
    }




}
