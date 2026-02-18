package com.clashcode.backend.repository;

import com.clashcode.backend.model.TestCaseFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TestCaseFileRepository extends JpaRepository<TestCaseFile, Long> {
    Optional<TestCaseFile> findByFilePath(String filePath);
    List<TestCaseFile> findByProblemId(Long problemId);
}