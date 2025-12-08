package com.clashcode.backend.service;

import com.clashcode.backend.dto.TestCaseResponseDto;
import com.clashcode.backend.judge.Judge0.Judge0Client;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import com.clashcode.backend.repository.TestCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final FileStorageService fileStorageService;
    private final Judge0Client judge0Client;
    public TestCaseService(TestCaseRepository testCaseRepository ,
                           FileStorageService fileStorageService,
                           Judge0Client judge0Client) {
        this.testCaseRepository = testCaseRepository;
        this.fileStorageService = fileStorageService;
        this.judge0Client = judge0Client;
    }

    public List<TestCase> addTestCases(List<MultipartFile> files , Problem problem , List<Boolean> visible) {
        List<TestCase> testCases = new ArrayList<>();
        for (int i = 0; i < files.size() ; i++) {
            TestCase testCase = TestCase.builder()
                                        .problem(problem)
                                        .visible(visible.get(i))
                                        .build();
            testCases.add(testCase);
        }
        testCases = testCaseRepository.saveAll(testCases);
        for (int i = 0; i < testCases.size(); i++) {
            TestCase testCase = testCases.get(i);
            MultipartFile file = files.get(i);

            String inputPath = fileStorageService.storeTestCase(
                    file,
                    problem.getId(),
                    testCase.getId()
            );

            testCase.setInputPath(inputPath);
            String input = fileStorageService.getTestCaseContent(inputPath);

            String expectedOutput = judge0Client.executeAndReturnOutput(
                    input,
                    problem.getSolution().getSolutionCode(),
                    problem.getSolution().getLanguageVersion().toString(),
                    problem.getTimeLimit(),
                    problem.getMemoryLimit()
            );

            String outputPath = fileStorageService.storeTestCaseOutput(
                    expectedOutput,
                    problem.getId(),
                    testCase.getId()
            );
            testCase.setOutputPath(outputPath);
        }
        return testCaseRepository.saveAll(testCases);
    }

    public List<TestCaseResponseDto> getVisibleTestCasesForProblem(Problem problem) {
        List<TestCaseResponseDto> visibleTestCases = new ArrayList<>();

        for (TestCase testCase : testCaseRepository.findByProblemAndVisibleTrue(problem)) {
            TestCaseResponseDto testCaseResponseDto = new TestCaseResponseDto();
            testCaseResponseDto.setInput(fileStorageService.getTestCaseContent(testCase.getInputPath()));
            testCaseResponseDto.setOutput(fileStorageService.getTestCaseContent(testCase.getOutputPath()));
            visibleTestCases.add(testCaseResponseDto);
        }

        return visibleTestCases;
    }

    public List<String> getInputTestCasesForProblem(Problem problem) {
        List<String> inputTestCases = new ArrayList<>();
        for (String path : testCaseRepository.findInputPathsByProblem(problem)) {
            inputTestCases.add(fileStorageService.getTestCaseContent(path));
        }
        return inputTestCases;
    }

    public List<String> getOutputTestCasesForProblem(Problem problem) {
        List<String> OutputTestCases = new ArrayList<>();
        for (String path : testCaseRepository.findOutputPathsByProblem(problem)) {
            OutputTestCases.add(fileStorageService.getTestCaseContent(path));
            System.out.println("Test" + fileStorageService.getTestCaseContent(path));
        }
        return OutputTestCases;
    }
}
