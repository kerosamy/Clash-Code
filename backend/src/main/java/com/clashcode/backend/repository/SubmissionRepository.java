package com.clashcode.backend.repository;

import com.clashcode.backend.enums.SubmissionStatus;
import com.clashcode.backend.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUserId(Long userId);
    List<Submission> findByUserIdAndMatchId(Long userId, Long matchId);

    @Query("""
                SELECT COUNT(DISTINCT s.problem.id)
                FROM Submission s
                WHERE s.user.id = :userId
           """)
    int countDistinctAttemptedProblems(@Param("userId") Long userId);

    @Query("""
                SELECT COUNT(DISTINCT s.problem.id)
                FROM Submission s
                WHERE s.user.id = :userId
                  AND s.status = :status
           """)
    int countDistinctSolvedProblems(@Param("userId") Long userId, @Param("status") SubmissionStatus status);

    @Query("""
            SELECT t AS category, COUNT(DISTINCT s.problem.id) AS count
            FROM Submission s
            JOIN s.problem.tags t
            WHERE s.user.id = :userId AND s.status = :status
            GROUP BY t
           """)
    List<Object[]> countProblemsByCategory(
            @Param("userId") Long userId,
            @Param("status") SubmissionStatus status
    );
}
