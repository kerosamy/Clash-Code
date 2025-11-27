package com.clashcode.backend.service;

import com.clashcode.backend.dto.TestCaseResponseDto;
import com.clashcode.backend.judge.Judge0.Judge0Client;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import com.clashcode.backend.repository.TestCaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestCaseServiceTest {

    private TestCaseRepository testCaseRepository;
    private FileStorageService fileStorageService;
    private TestCaseService testCaseService;

    @BeforeEach
    void setUp() {
        testCaseRepository = mock(TestCaseRepository.class);
        fileStorageService = mock(FileStorageService.class);
        Judge0Client judge0Client = mock(Judge0Client.class);
        testCaseService = new TestCaseService(testCaseRepository, fileStorageService, judge0Client);
    }

    @Test
    void testAddTestCases() {
        Problem problem = new Problem();
        problem.setId(1L);

        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(file1, file2);

        List<Boolean> visibleFlags = List.of(true, false);

        // Mock repository saveAll to return test cases with IDs
        TestCase tc1 = TestCase.builder().problem(problem).visible(true).id(101L).build();
        TestCase tc2 = TestCase.builder().problem(problem).visible(false).id(102L).build();
        when(testCaseRepository.saveAll(any())).thenReturn(List.of(tc1, tc2));

        // Mock file storage
        when(fileStorageService.storeTestCase(file1, 1L, 101L)).thenReturn("path/input1.txt");
        when(fileStorageService.storeTestCase(file2, 1L, 102L)).thenReturn("path/input2.txt");

        List<TestCase> result = testCaseService.addTestCases(files, problem, visibleFlags);

        // Verify repository calls
        verify(testCaseRepository, times(2)).saveAll(any());

        // Verify file storage calls
        verify(fileStorageService).storeTestCase(file1, 1L, 101L);
        verify(fileStorageService).storeTestCase(file2, 1L, 102L);

        // Assertions
        assertEquals(2, result.size());
        assertEquals("path/input1.txt", result.get(0).getInputPath());
        assertEquals("path/input2.txt", result.get(1).getInputPath());
    }

    @Test
    void testGetVisibleTestCasesForProblem() {
        Problem problem = new Problem();
        problem.setId(1L);

        // Mock test cases with input/output paths
        TestCase tc1 = TestCase.builder()
                .id(101L)
                .problem(problem)
                .visible(true)
                .inputPath("path/to/input1.txt")
                .outputPath("path/to/output1.txt")
                .build();

        TestCase tc2 = TestCase.builder()
                .id(102L)
                .problem(problem)
                .visible(true)
                .inputPath("path/to/input2.txt")
                .outputPath("path/to/output2.txt")
                .build();

        when(testCaseRepository.findByProblemAndVisibleTrue(problem)).thenReturn(List.of(tc1, tc2));

        TestCaseResponseDto dto1 = new TestCaseResponseDto("input1", "output1");
        TestCaseResponseDto dto2 = new TestCaseResponseDto("input2", "output2");

        when(fileStorageService.getTestCaseContent("path/to/input1.txt")).thenReturn("input1");
        when(fileStorageService.getTestCaseContent("path/to/output1.txt")).thenReturn("output1");
        when(fileStorageService.getTestCaseContent("path/to/input2.txt")).thenReturn("input2");
        when(fileStorageService.getTestCaseContent("path/to/output2.txt")).thenReturn("output2");

        List<TestCaseResponseDto> result = testCaseService.getVisibleTestCasesForProblem(problem);

        // Verify repository and file storage calls
        verify(testCaseRepository).findByProblemAndVisibleTrue(problem);
        verify(fileStorageService).getTestCaseContent("path/to/input1.txt");
        verify(fileStorageService).getTestCaseContent("path/to/output1.txt");
        verify(fileStorageService).getTestCaseContent("path/to/input2.txt");
        verify(fileStorageService).getTestCaseContent("path/to/output2.txt");

        // Check result
        assertEquals(2, result.size());
        assertEquals("input1", result.get(0).getInput());
        assertEquals("output1", result.get(0).getOutput());
        assertEquals("input2", result.get(1).getInput());
        assertEquals("output2", result.get(1).getOutput());
    }

}
