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
            return null; // or throw a custom runtime exception if you prefer
        }
    }

    public String getTestCaseContent(String testCasePathStr) {
        if (testCasePathStr == null || testCasePathStr.isBlank()) {
            return null; // avoid NullPointerException
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

            // Write string content to file
            Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            return filePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null; // or throw a custom runtime exception
        }
    }





}
