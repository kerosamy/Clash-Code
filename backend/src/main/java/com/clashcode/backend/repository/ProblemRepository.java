package com.clashcode.backend.repository;

import com.clashcode.backend.enums.ProblemStatus;
import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.model.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    Page<Problem> findByProblemStatus(ProblemStatus status, Pageable pageable);
    Page<Problem> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT p FROM Problem p WHERE p.rate BETWEEN :minRate AND :maxRate")
    Page<Problem> findByRateBetween(@Param("minRate") Integer minRate,
                                    @Param("maxRate") Integer maxRate,
                                    Pageable pageable);

    @Query("""
                SELECT p
                FROM Problem p
                JOIN p.tags t
                WHERE t IN :tags
                GROUP BY p
                HAVING COUNT(DISTINCT t) = :tagsSize
           """)
    Page<Problem> findByTags(@Param("tags") List<ProblemTags> tags,
                             @Param("tagsSize") long tagsSize,
                             Pageable pageable);

    @Query("""
                SELECT p
                FROM Problem p
                JOIN p.tags t
                WHERE t IN :tags
                  AND p.rate BETWEEN :minRate AND :maxRate
                GROUP BY p
                HAVING COUNT(DISTINCT t) = :tagsSize
           """)
    Page<Problem> findByTagsAndRateRange(
            @Param("tags") List<ProblemTags> tags,
            @Param("tagsSize") long tagsSize,
            @Param("minRate") Integer minRate,
            @Param("maxRate") Integer maxRate,
            Pageable pageable);
}
