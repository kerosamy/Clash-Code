package com.clashcode.backend.service;

import com.clashcode.backend.model.Match;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class MatchSchedulerTest {

    @Mock
    private MatchService matchService;

    private MatchScheduler matchScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        matchScheduler = new MatchScheduler(matchService);
        matchScheduler.init();
    }

    @Test
    void scheduleMatchEnd_FutureEndTime_SchedulesTask() {
        Match match = Match.builder()
                .id(1L)
                .startAt(java.time.LocalDateTime.now())
                .duration(30)
                .build();

        ScheduledFuture<?> result = matchScheduler.scheduleMatchEnd(match);

        assertNotNull(result);
        assertFalse(result.isDone());
    }

    @Test
    void scheduleMatchEnd_PastEndTime_CompletesImmediately() {
        Match match = Match.builder()
                .id(1L)
                .startAt(java.time.LocalDateTime.now().minusHours(1))
                .duration(30)
                .build();

        ScheduledFuture<?> result = matchScheduler.scheduleMatchEnd(match);

        assertNull(result);
        verify(matchService).completeMatch(match, null);
    }

    @Test
    void init_InitializesScheduler() {
        MatchScheduler newScheduler = new MatchScheduler(matchService);

        assertDoesNotThrow(() -> newScheduler.init());
    }
}