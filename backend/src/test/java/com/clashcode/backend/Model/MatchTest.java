package com.clashcode.backend.Model;

import com.clashcode.backend.enums.*;
import com.clashcode.backend.model.Match;
import com.clashcode.backend.model.MatchParticipant;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.Submission;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    @Test
    void testMatchGettersAndSetters() {
        Match match = new Match();
        LocalDateTime now = LocalDateTime.now();
        Problem problem = new Problem();
        List<Submission> submissions = new ArrayList<>();
        List<MatchParticipant> participants = new ArrayList<>();

        match.setId(1L);
        match.setStartAt(now);
        match.setDuration(60);
        match.setMatchState(MatchState.ONGOING);
        match.setGameMode(GameMode.RATED);
        match.setProblem(problem);
        match.setSubmissions(submissions);
        match.setParticipants(participants);

        assertEquals(1L, match.getId());
        assertEquals(now, match.getStartAt());
        assertEquals(60, match.getDuration());
        assertEquals(MatchState.ONGOING, match.getMatchState());
        assertEquals(GameMode.RATED, match.getGameMode());
        assertEquals(problem, match.getProblem());
        assertEquals(submissions, match.getSubmissions());
        assertEquals(participants, match.getParticipants());
    }

    @Test
    void testMatchBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Problem problem = new Problem();

        Match match = Match.builder()
                .id(1L)
                .startAt(now)
                .duration(45)
                .matchState(MatchState.COMPLETED)
                .gameMode(GameMode.RATED)
                .problem(problem)
                .build();

        assertEquals(1L, match.getId());
        assertEquals(now, match.getStartAt());
        assertEquals(45, match.getDuration());
        assertEquals(MatchState.COMPLETED, match.getMatchState());
        assertEquals(GameMode.RATED, match.getGameMode());
        assertEquals(problem, match.getProblem());
        assertNotNull(match.getSubmissions());
        assertNotNull(match.getParticipants());
    }

    @Test
    void testMatchBuilderDefaults() {
        Match match = Match.builder()
                .duration(30)
                .matchState(MatchState.ONGOING)
                .gameMode(GameMode.UNRATED)
                .problem(new Problem())
                .build();

        assertNotNull(match.getStartAt());
        assertNotNull(match.getSubmissions());
        assertNotNull(match.getParticipants());
        assertTrue(match.getSubmissions().isEmpty());
        assertTrue(match.getParticipants().isEmpty());
    }

    @Test
    void testMatchNoArgsConstructor() {
        Match match = new Match();
        assertNotNull(match);
    }

    @Test
    void testMatchAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Problem problem = new Problem();
        List<Submission> submissions = new ArrayList<>();
        List<MatchParticipant> participants = new ArrayList<>();

        Match match = new Match(1L, now, 60, MatchState.ONGOING, GameMode.UNRATED, problem, submissions, participants);

        assertEquals(1L, match.getId());
        assertEquals(now, match.getStartAt());
        assertEquals(60, match.getDuration());
        assertEquals(MatchState.ONGOING, match.getMatchState());
        assertEquals(GameMode.UNRATED, match.getGameMode());
        assertEquals(problem, match.getProblem());
        assertEquals(submissions, match.getSubmissions());
        assertEquals(participants, match.getParticipants());
    }
}