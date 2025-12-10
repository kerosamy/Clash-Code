package com.clashcode.backend.repository;

import com.clashcode.backend.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUserId(Long userId);
    List<Submission> findByUserIdAndMatchId(Long userId, Long matchId);

}
