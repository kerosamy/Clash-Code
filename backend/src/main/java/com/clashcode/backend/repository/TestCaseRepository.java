package com.clashcode.backend.repository;

import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findByProblemAndVisibleTrue(Problem problem);

}
