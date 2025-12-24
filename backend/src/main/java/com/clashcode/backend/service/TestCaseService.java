package com.clashcode.backend.service;

import com.clashcode.backend.dto.TestCaseResponseDto;
import com.clashcode.backend.dto.TestcaseRunRequestDto;
import com.clashcode.backend.judge.Judge0.Judge0Client;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import com.clashcode.backend.repository.TestCaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final TestCasesFileStorageService testCasesFileStorageService;
    private final Judge0Client judge0Client;
    public TestCaseService(TestCaseRepository testCaseRepository ,
                           TestCasesFileStorageService testCasesFileStorageService,
                           Judge0Client judge0Client) {
        this.testCaseRepository = testCaseRepository;
        this.testCasesFileStorageService = testCasesFileStorageService;
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

            String inputPath = testCasesFileStorageService.storeTestCase(
                    file,
                    problem.getId(),
                    testCase.getId()
            );

            testCase.setInputPath(inputPath);
            String input = testCasesFileStorageService.getTestCaseContent(inputPath);

            String expectedOutput = judge0Client.executeAndReturnOutput(
                    input,
                    problem.getSolution().getSolutionCode(),
                    problem.getSolution().getLanguageVersion().toString(),
                    problem.getTimeLimit(),
                    problem.getMemoryLimit()
            );

            String outputPath = testCasesFileStorageService.storeTestCaseOutput(
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
            testCaseResponseDto.setInput(testCasesFileStorageService.getTestCaseContent(testCase.getInputPath()));
            testCaseResponseDto.setOutput(testCasesFileStorageService.getTestCaseContent(testCase.getOutputPath()));
            visibleTestCases.add(testCaseResponseDto);
        }

        return visibleTestCases;
    }

    public List<String> getInputTestCasesForProblem(Problem problem) {
        List<String> inputTestCases = new ArrayList<>();
        for (String path : testCaseRepository.findInputPathsByProblem(problem)) {
            inputTestCases.add(testCasesFileStorageService.getTestCaseContent(path));
        }
        return inputTestCases;
    }

    public List<String> getOutputTestCasesForProblem(Problem problem) {
        List<String> OutputTestCases = new ArrayList<>();
        for (String path : testCaseRepository.findOutputPathsByProblem(problem)) {
            OutputTestCases.add(testCasesFileStorageService.getTestCaseContent(path));
            System.out.println("Test" + testCasesFileStorageService.getTestCaseContent(path));
        }
        return OutputTestCases;
    }

    @Transactional
    public void deleteByProblem(Problem problem) {
        testCasesFileStorageService.deleteTestCasesDirectory(problem.getId());
        testCaseRepository.deleteByProblem(problem);
    }

    public List<String> runTestCases(TestcaseRunRequestDto testcaseRunRequestDto) {
        List<String> outputs = new ArrayList<>();
        for (String input : testcaseRunRequestDto.getStdin()) {
            String output = judge0Client.executeAndReturnOutput(
                    input,
                    testcaseRunRequestDto.getSourceCode(),
                    testcaseRunRequestDto.getLanguage(),
                    testcaseRunRequestDto.getTimeLimit(),
                    testcaseRunRequestDto.getMemoryLimit()
            );
            outputs.add(output);
        }
        return outputs;
    }
}
