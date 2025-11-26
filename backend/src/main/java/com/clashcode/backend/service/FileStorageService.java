package com.clashcode.backend.service;

import com.clashcode.backend.dto.TestCaseResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileStorageService {
    @Value("${clashcode.filesystem.base-path}")
    private String basePath ;

    public String storeTestCase(MultipartFile file,
                                Long problemId,
                                Long testCaseId,
                                boolean isInput) {
        try {
            Path problemDir = Paths.get(basePath, String.valueOf(problemId));
            if (!Files.exists(problemDir)) {
                Files.createDirectories(problemDir);
            }

            String fileName = "testcase_" + testCaseId + "_" +
                    (isInput ? "input" : "output") + ".txt";

            Path filePath = problemDir.resolve(fileName);
            Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE_NEW);

            return filePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null; // or throw a custom runtime exception if you prefer
        }
    }


    public TestCaseResponseDto getTestCaseContent(Long problemId, Long testCaseId) {
        Path problemDir = Paths.get(basePath, String.valueOf(problemId));

        try {
            if (!Files.exists(problemDir) || !Files.isDirectory(problemDir)) {
                throw new IOException("Problem folder does not exist");
            }

            Path inputFile = problemDir.resolve("testcase_" + testCaseId + "_input.txt");
            Path outputFile = problemDir.resolve("testcase_" + testCaseId + "_output.txt");

            String inputContent = Files.exists(inputFile) ? Files.readString(inputFile) : null;
            String outputContent = Files.exists(outputFile) ? Files.readString(outputFile) : null;

            return new TestCaseResponseDto(inputContent, outputContent);

        } catch (IOException e) {
            System.err.println("Error reading test case: " + e.getMessage());
            return new TestCaseResponseDto(null, null);
        }
    }

}
