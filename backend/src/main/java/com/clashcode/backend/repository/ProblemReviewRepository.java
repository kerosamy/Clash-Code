package com.clashcode.backend.repository;

import com.clashcode.backend.model.ProblemReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProblemReviewRepository
        extends JpaRepository<ProblemReview, Long> {

    Optional<ProblemReview> findByProblemId(Long problemId);
}
