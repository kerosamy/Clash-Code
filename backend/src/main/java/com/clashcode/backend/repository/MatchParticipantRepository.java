package com.clashcode.backend.repository;

import com.clashcode.backend.model.MatchParticipant;
import com.clashcode.backend.model.MatchParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MatchParticipantRepository extends JpaRepository<MatchParticipant, MatchParticipantId> {
    @Query("""
                SELECT COUNT(mp)
                FROM MatchParticipant mp
                WHERE mp.user.id = :userId
           """)
    int countMatches(Long userId);

    @Query("""
                SELECT COUNT(mp)
                FROM MatchParticipant mp
                WHERE mp.user.id = :userId AND mp.rank = 1
           """)
    int countWonMatches(Long userId);
}
