package com.clashcode.backend.repository;

import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findByProblemAndVisibleTrue(Problem problem);

    @Query("SELECT t.inputPath FROM TestCase t WHERE t.problem = :problem")
    List<String> findInputPathsByProblem(@Param("problem") Problem problem);

    @Query("SELECT t.outputPath FROM TestCase t WHERE t.problem = :problem")
    List<String> findOutputPathsByProblem(@Param("problem") Problem problem);

}
