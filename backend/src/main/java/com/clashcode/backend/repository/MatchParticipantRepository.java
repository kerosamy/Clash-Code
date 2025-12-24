package com.clashcode.backend.repository;

import com.clashcode.backend.model.MatchParticipant;
import com.clashcode.backend.model.MatchParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    @Query("""
                  SELECT mp
                  FROM MatchParticipant mp
                  JOIN Match m ON mp.match.id = m.id
                  JOIN Problem p ON m.problem.id = p.id
                  WHERE mp.user.id = :userId
                  AND m.matchState = 'COMPLETED'
                  AND (
                       :rated IS NULL
                       OR (:rated = true  AND m.gameMode = 'RATED')
                       OR (:rated = false AND m.gameMode <> 'RATED')
                  )
                  ORDER BY m.startAt DESC
            """)
    Page<MatchParticipant> findHistoryByUserId(
            @Param("userId") Long userId,
            @Param("rated") Boolean rated,
            Pageable pageable);
}
