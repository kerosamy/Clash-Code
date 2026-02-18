package com.clashcode.backend.service;

import com.clashcode.backend.model.TestCaseFile;
import com.clashcode.backend.repository.TestCaseFileRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@Primary
public class TestCasesDBStorageService implements TestCasesStorageService {

    private final TestCaseFileRepository testCaseFileRepository;

    TestCasesDBStorageService(TestCaseFileRepository testCaseFileRepository) {
        this.testCaseFileRepository = testCaseFileRepository;
    }

    public String storeTestCase(MultipartFile file,
                                Long problemId,
                                Long testCaseId) {
        try {
            // Read file content
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);

            // Create virtual path (same format as before)
            String virtualPath = problemId + "/testcase_" + testCaseId + "_input.txt";

            // Create entity
            TestCaseFile testCaseFile = new TestCaseFile();
            testCaseFile.setProblemId(problemId);
            testCaseFile.setTestCaseId(testCaseId);
            testCaseFile.setFileType("input");
            testCaseFile.setContent(content);
            testCaseFile.setFilePath(virtualPath);

            // Save to database
            testCaseFileRepository.save(testCaseFile);

            return virtualPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTestCaseContent(String testCasePathStr) {
        System.out.println("Getting test case content");
        System.out.println(testCasePathStr);
        if (testCasePathStr == null || testCasePathStr.isBlank()) {
            return null;
        }
        try {
            return testCaseFileRepository.findByFilePath(testCasePathStr.trim())
                    .map(TestCaseFile::getContent)
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("Error reading test case: " + e.getMessage());
            return null;
        }
    }

    public String storeTestCaseOutput(String content, Long problemId, Long testCaseId) {
        try {
            // Create virtual path
            String virtualPath = problemId + "/testcase_" + testCaseId + "_output.txt";

            // Check if already exists (for update)
            TestCaseFile testCaseFile = testCaseFileRepository.findByFilePath(virtualPath)
                    .orElse(new TestCaseFile());

            testCaseFile.setProblemId(problemId);
            testCaseFile.setTestCaseId(testCaseId);
            testCaseFile.setFileType("output");
            testCaseFile.setContent(content);
            testCaseFile.setFilePath(virtualPath);

            testCaseFileRepository.save(testCaseFile);

            return virtualPath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteTestCasesDirectory(Long problemId) {
        try {
            // Find and delete all files for this problem
            testCaseFileRepository.findByProblemId(problemId)
                    .forEach(testCaseFileRepository::delete);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
