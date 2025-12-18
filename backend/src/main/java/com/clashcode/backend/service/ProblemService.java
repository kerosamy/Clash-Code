package com.clashcode.backend.service;

import com.clashcode.backend.dto.ProblemListDto;
import com.clashcode.backend.dto.ProblemRequestDto;
import com.clashcode.backend.dto.ProblemResponseDto;
import com.clashcode.backend.dto.TestCaseResponseDto;
import com.clashcode.backend.enums.ProblemStatus;
import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.mapper.ProblemMapper;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.TestCase;
import com.clashcode.backend.repository.ProblemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProblemService {
    int present = 0;
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

    public void addProblem(
            ProblemRequestDto dto,
            List<MultipartFile> files,
            String username
    ) {
        long id = dto.getId();
        Problem problem;

        // CREATE
        if (id == present) {
            problem = problemMapper.toProblem(dto);
            problem.setAuthor(username);
            problemRepository.save(problem);
        }
        // UPDATE
        else {
            problem = problemRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Problem not found"));

            problemMapper.updateProblem(problem, dto);
            problem.setProblemStatus(ProblemStatus.PENDING_APPROVAL);
            problemRepository.save(problem);

            testCaseService.deleteByProblem(problem);
            problem.getTestCases().clear();
            problemRepository.saveAndFlush(problem);
        }

        // COMMON: add test cases
        List<TestCase> testCases =
                testCaseService.addTestCases(files, problem, dto.getVisibleFlags());

        problem.setTestCases(testCases);
        problemRepository.save(problem);
    }


    public ProblemResponseDto getProblemById (Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        List<TestCaseResponseDto> visibleTestCases = testCaseService.getVisibleTestCasesForProblem(problem);
        return problemMapper.toResponseDto(problem, visibleTestCases);
    }


    public Page<ProblemListDto> getPendingProblems(int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        return problemRepository.findByProblemStatus(ProblemStatus.PENDING_APPROVAL, pageRequest)
                .map(problemMapper::toListDto);
    }

    public void acceptProblem(Long id) {
        Problem p = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        p.setProblemStatus(ProblemStatus.APPROVED);
        problemRepository.save(p);
    }

    public void rejectProblem(Long id, String note) {
        Problem p = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        p.setProblemStatus(ProblemStatus.REJECTED);
        problemRepository.save(p);
    }

    public Page<ProblemListDto> getApprovedProblems(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return problemRepository.findByProblemStatus(ProblemStatus.APPROVED, pageRequest)
                .map(problemMapper::toListDto);
    }



    public Page<ProblemListDto> getFilteredProblems(
            List<ProblemTags> tags,
            Integer minRate,
            Integer maxRate,
            int page,
            int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);

        if (tags != null && tags.isEmpty()) tags = null;

        Page<Problem> problemPage;

        boolean hasTags = tags != null;
        boolean hasMin = minRate != null;
        boolean hasMax = maxRate != null;

        if (hasTags && hasMin && hasMax) {
            problemPage = problemRepository.findByTagsAndRateRange(tags, tags.size(), minRate, maxRate, pageRequest);
        }
        else if (hasTags && (hasMin || hasMax)) {
            problemPage = problemRepository.findByTagsAndRateRange(
                    tags, tags.size(),
                    minRate != null ? minRate : 0,
                    maxRate != null ? maxRate : Integer.MAX_VALUE,
                    pageRequest
            );
        }
        else if (hasTags) {
            problemPage = problemRepository.findByTags(tags, tags.size(), pageRequest);
        }
        else if (hasMin && hasMax) {
            problemPage = problemRepository.findByRateBetween(minRate, maxRate, pageRequest);
        }
        else {
            problemPage = problemRepository.findAll(pageRequest);
        }

        return problemPage.map(problemMapper::toListDto);
    }

    public Page<ProblemListDto> searchProblemsByName(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return problemRepository.findByTitleContainingIgnoreCase(keyword, pageRequest)
                .map(problemMapper::toListDto);
    }

    public Page<ProblemListDto> getRejectedProblems(int page, int size, String username) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return problemRepository.findByStatusAndUsername(
                ProblemStatus.REJECTED,
                username,
                pageRequest
        ).map(problemMapper::toListDto);
    }

}
