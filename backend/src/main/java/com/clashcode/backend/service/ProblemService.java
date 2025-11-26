package com.clashcode.backend.service;
import com.clashcode.backend.dto.ProblemListDto;
import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponseDto;
import com.clashcode.backend.dto.TestCaseResponseDto;
import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.mapper.ProblemMapper;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import com.clashcode.backend.repository.ProblemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final TestCaseService testCaseService;
    private final ProblemMapper problemMapper;

    public ProblemService(ProblemRepository problemRepository,
                          TestCaseService testCaseService,
                          ProblemMapper problemMapper) {

        this.problemMapper = problemMapper;
        this.problemRepository = problemRepository;
        this.testCaseService = testCaseService;
    }

    public ProblemResponseDto getProblemById (Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        List<TestCaseResponseDto> visibleTestCases = testCaseService.getVisibleTestCasesForProblem(problem);
        return problemMapper.toResponseDto(problem, visibleTestCases);
    }

    public void addProblem (ProblemRequestDto problemRequestDto,
                            List<MultipartFile> files){

        Problem problem = problemMapper.toProblem(problemRequestDto);
        problemRepository.save(problem);

        List<TestCase> testCases = testCaseService.addTestCases(files,problem,problemRequestDto.getVisibleFlags());

        problem.setTestCases(testCases);
        problemRepository.save(problem);
    }

    public Page<ProblemListDto> getAllProblems(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return problemRepository.findAll(pageRequest)
                .map(problemMapper::toListDto);
    }

    public Page<ProblemListDto> getFilteredProblems(List<ProblemTags> tags, Integer rate, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        if (tags != null && tags.isEmpty()) {
            tags = null;
        }

        Page<Problem> problemPage;
        long tagsSize = tags != null ? tags.size() : 0;

        if (tags != null && rate != null) {
            problemPage = problemRepository.findByTagsAndRate(tags, tagsSize, rate, pageRequest);
        } else if (tags != null) {
            problemPage = problemRepository.findByTags(tags, tagsSize, pageRequest);
        } else if (rate != null) {
            problemPage = problemRepository.findByRate(rate, pageRequest);
        } else {
            problemPage = problemRepository.findAll(pageRequest);
        }

        return problemPage.map(problemMapper::toListDto);
    }


}
