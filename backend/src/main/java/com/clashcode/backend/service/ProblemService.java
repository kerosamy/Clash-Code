package com.clashcode.backend.service;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.ProblemStatus;
import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.judge.Judge0.Judge0Client;
import com.clashcode.backend.mapper.ProblemMapper;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.ProblemReview;
import com.clashcode.backend.model.TestCase;
import com.clashcode.backend.repository.ProblemRepository;
import com.clashcode.backend.repository.ProblemReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProblemService {
    int present = 0;
    private final ProblemRepository problemRepository;
    private final TestCaseService testCaseService;
    private final ProblemMapper problemMapper;
    private final ProblemReviewRepository problemReviewRepository;
    private final Judge0Client judge0Client;

    public ProblemService(ProblemRepository problemRepository,
                          TestCaseService testCaseService,
                          ProblemMapper problemMapper,
                          ProblemReviewRepository problemReviewRepository,
                          Judge0Client judge0Client) {

        this.problemMapper = problemMapper;
        this.problemRepository = problemRepository;
        this.testCaseService = testCaseService;
        this.problemReviewRepository = problemReviewRepository;
        this.judge0Client = judge0Client;
    }

    public void addProblem(
            ProblemRequestDto dto,
            List<MultipartFile> files,
            String username
    ) {
        Problem problem = createOrUpdateProblem(dto, username);

        List<TestCase> testCases = handleTestCases(problem, files, dto.getVisibleFlags());
        problem.setTestCases(testCases);

        problemRepository.save(problem);
    }

    private Problem createOrUpdateProblem(ProblemRequestDto dto, String username) {
        long id = dto.getId();

        if (id == present) {
            return createProblem(dto, username);
        } else {
            return updateProblem(dto);
        }
    }

    private Problem createProblem(ProblemRequestDto dto, String username) {
        Problem problem = problemMapper.toProblem(dto);
        problem.setAuthor(username);
        problemRepository.save(problem);
        return problem;
    }

    private Problem updateProblem(ProblemRequestDto dto) {
        Problem problem = problemRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        problemMapper.updateProblem(problem, dto);
        problem.setProblemStatus(ProblemStatus.PENDING_APPROVAL);

        testCaseService.deleteByProblem(problem);
        problem.getTestCases().clear();

        problemRepository.saveAndFlush(problem);
        return problem;
    }

    private List<TestCase> handleTestCases(
            Problem problem,
            List<MultipartFile> files,
            List<Boolean> visibleFlags
    ) {
        return testCaseService.addTestCases(files, problem, visibleFlags);
    }



    public PartialProblemResponseDto getPartialProblemById (Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        List<TestCaseResponseDto> visibleTestCases = testCaseService.getVisibleTestCasesForProblem(problem);
        return problemMapper.toPartialResponseDto(problem, visibleTestCases);
    }


    public FullProblemResponseDto getFullProblemById (Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        List<TestCaseResponseDto> visibleTestCases = testCaseService.getVisibleTestCasesForProblem(problem);
        return problemMapper.toFullResponseDto(problem, visibleTestCases);
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

        problemReviewRepository.findByProblemId(id)
                .ifPresent(problemReviewRepository::delete);
    }

    @Transactional
    public void rejectProblem(Long id, String note) {

        Problem p = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found"));

        // update problem status
        p.setProblemStatus(ProblemStatus.REJECTED);

        // insert or update review row
        ProblemReview review = problemReviewRepository
                .findByProblemId(id)
                .orElse(new ProblemReview());

        review.setProblemId(id);
        review.setNote(note);
        review.setReviewedAt(LocalDateTime.now());

        problemReviewRepository.save(review);
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
        ).map(problem -> {
            String rejectionNote = problemReviewRepository
                    .findByProblemId(problem.getId())
                    .map(ProblemReview::getNote)
                    .orElse(null);

            return problemMapper.toListDto(problem, rejectionNote);
        });
    }

    public Page<ProblemListDto> getMySuggestedProblems(String username, ProblemStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Problem> problems = problemRepository.findByAuthorAndStatus(username, status, pageable);
        return problems.map(problem -> {
            String rejectionNote = null;
            if (problem.getProblemStatus() == ProblemStatus.REJECTED) {
                rejectionNote = problemReviewRepository
                        .findByProblemId(problem.getId())
                        .map(ProblemReview::getNote)
                        .orElse(null);

                System.out.println("Problem ID: " + problem.getId() + ", rejectionNote: " + rejectionNote);
            }
            return problemMapper.toListDto(problem, rejectionNote);
        });
    }
}
