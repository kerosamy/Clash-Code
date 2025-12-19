package com.clashcode.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class TestCasesFileStorageService {
    @Value("${clashcode.filesystem.base-path}")
    private String basePath ;

    public String storeTestCase(MultipartFile file,
                                Long problemId,
                                Long testCaseId) {
        try {
            Path problemDir = Paths.get(basePath, String.valueOf(problemId));
            if (!Files.exists(problemDir)) {
                Files.createDirectories(problemDir);
            }

            String fileName = "testcase_" + testCaseId + "_" + "input" + ".txt";

            Path filePath = problemDir.resolve(fileName);
            Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE_NEW);

            return filePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTestCaseContent(String testCasePathStr) {
        if (testCasePathStr == null || testCasePathStr.isBlank()) {
            return null;
        }

        try {
            Path testCasePath = Paths.get(testCasePathStr.trim());
            return Files.exists(testCasePath) ? Files.readString(testCasePath) : null;
        } catch (IOException e) {
            System.err.println("Error reading test case: " + e.getMessage());
            return null;
        }
    }

    public String storeTestCaseOutput(String content, Long problemId, Long testCaseId) {
        try {
            Path problemDir = Paths.get(basePath, String.valueOf(problemId));
            if (!Files.exists(problemDir)) {
                Files.createDirectories(problemDir);
            }

            String fileName = "testcase_" + testCaseId + "_output.txt";
            Path filePath = problemDir.resolve(fileName);

            Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return filePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteTestCasesDirectory(Long problemId) {
        try {
            Path problemDir = Paths.get(basePath, String.valueOf(problemId));
            if (Files.exists(problemDir)) {
                Files.walk(problemDir)
                        .sorted((a, b) -> b.compareTo(a)) // Delete files before directory
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                System.err.println("Failed to delete file: " + path);
                            }
                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
