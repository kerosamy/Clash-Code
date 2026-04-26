package com.clashcode.backend.Model;

import com.clashcode.backend.model.Match;
import com.clashcode.backend.model.MatchParticipant;
import com.clashcode.backend.model.MatchParticipantId;
import com.clashcode.backend.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchParticipantTest {

    @Test
    void testMatchParticipantGettersAndSetters() {
        MatchParticipant participant = new MatchParticipant();
        MatchParticipantId id = new MatchParticipantId(1L, 2L);
        User user = new User();
        Match match = new Match();

        participant.setId(id);
        participant.setUser(user);
        participant.setMatch(match);
        participant.setRank(1);
        participant.setRateChange(50);
        participant.setNewRating(1500);

        assertEquals(id, participant.getId());
        assertEquals(user, participant.getUser());
        assertEquals(match, participant.getMatch());
        assertEquals(1, participant.getRank());
        assertEquals(50, participant.getRateChange());
        assertEquals(1500, participant.getNewRating());
    }

    @Test
    void testMatchParticipantBuilder() {
        MatchParticipantId id = new MatchParticipantId(1L, 2L);
        User user = new User();
        Match match = new Match();

        MatchParticipant participant = MatchParticipant.builder()
                .id(id)
                .user(user)
                .match(match)
                .rank(2)
                .rateChange(-20)
                .newRating(1400)
                .build();

        assertEquals(id, participant.getId());
        assertEquals(user, participant.getUser());
        assertEquals(match, participant.getMatch());
        assertEquals(2, participant.getRank());
        assertEquals(-20, participant.getRateChange());
        assertEquals(1400, participant.getNewRating());
    }

    @Test
    void testMatchParticipantNoArgsConstructor() {
        MatchParticipant participant = new MatchParticipant();
        assertNotNull(participant);
    }

    @Test
    void testMatchParticipantAllArgsConstructor() {
        MatchParticipantId id = new MatchParticipantId(1L, 2L);
        User user = new User();
        Match match = new Match();

        MatchParticipant participant = new MatchParticipant(id, user, match, 1, 50, 1500);

        assertEquals(id, participant.getId());
        assertEquals(user, participant.getUser());
        assertEquals(match, participant.getMatch());
        assertEquals(1, participant.getRank());
        assertEquals(50, participant.getRateChange());
        assertEquals(1500, participant.getNewRating());
    }
}
