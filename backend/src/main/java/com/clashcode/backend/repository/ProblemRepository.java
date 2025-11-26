package com.clashcode.backend.repository;

import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.model.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    @Query("SELECT DISTINCT p FROM Problem p WHERE p.rate = :rate")
    Page<Problem> findByRate(@Param("rate") Integer rate,
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
        AND p.rate = :rate
        GROUP BY p
        HAVING COUNT(DISTINCT t) = :tagsSize
    """)
    Page<Problem> findByTagsAndRate(
            @Param("tags") List<ProblemTags> tags,
            @Param("tagsSize") long tagsSize,
            @Param("rate") Integer rate,
            Pageable pageable
    );
}
