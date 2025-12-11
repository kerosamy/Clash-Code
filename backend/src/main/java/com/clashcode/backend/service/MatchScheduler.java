package com.clashcode.backend.service;

import com.clashcode.backend.model.Match;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;

@Service
public class MatchScheduler {

    private final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    private final MatchService matchService;

    public MatchScheduler(@Lazy MatchService matchService) {
        this.matchService = matchService;
    }

    @PostConstruct
    public void init() {
        scheduler.setPoolSize(10);
        scheduler.initialize();
    }

    public ScheduledFuture<?> scheduleMatchEnd(Match match) {
        LocalDateTime endTime = match.getStartAt().plusMinutes(match.getDuration());
        long delay = Duration.between(LocalDateTime.now(), endTime).toMillis();

        if (delay <= 0) {
            // Already expired
            System.out.println("terminateee!!");
            matchService.completeMatch(match, null);
            return null;
        }

        return scheduler.schedule(() -> matchService.completeMatch(match, null),
                java.util.Date.from(endTime.atZone(java.time.ZoneId.systemDefault()).toInstant()));
    }
}