//package com.clashcode.backend.repository;
//
//import com.clashcode.backend.BaseTest;
//import com.clashcode.backend.enums.LanguageVersion;
//import com.clashcode.backend.enums.ProblemTags;
//import com.clashcode.backend.model.Problem;
//import com.clashcode.backend.model.Solution;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@Transactional
//class ProblemRepositoryTest extends BaseTest {
//
//    @Autowired
//    private ProblemRepository problemRepository;
//
//    @BeforeEach
//    void setUp() {
//        // Clear all data before each test to ensure isolation
//        problemRepository.deleteAll();
//    }
//
//    // Helper to create Problem
//    private Problem createProblem(String title, int rate, List<ProblemTags> tags) {
//        Problem problem = new Problem();
//        problem.setTitle(title);
//        problem.setRate(rate);
//        problem.setTimeLimit(250);
//        problem.setMemoryLimit(4);
//        problem.setInputFormat("Standard input");
//        problem.setOutputFormat("Standard output");
//        problem.setStatement("Problem statement for " + title);
//
//        Solution solution = new Solution();
//        solution.setLanguageVersion(LanguageVersion.JAVA_OPENJDK_13);
//        solution.setSolutionCode("// Sample solution code");
//        problem.setSolution(solution);
//
//        if (tags != null) {
//            problem.setTags(tags);
//        }
//        return problem;
//    }
//
//    @Test
//    void testFindByRateBetween() {
//        Problem p1 = createProblem("Easy Problem", 100, null);
//        Problem p2 = createProblem("Medium Problem", 500, null);
//        Problem p3 = createProblem("Hard Problem", 1000, null);
//        problemRepository.saveAll(List.of(p1, p2, p3));
//
//        var page = problemRepository.findByRateBetween(100, 500, PageRequest.of(0, 10));
//
//        assertThat(page.getContent()).hasSize(2);
//        assertThat(page.getContent())
//                .extracting(Problem::getTitle)
//                .containsExactlyInAnyOrder("Easy Problem", "Medium Problem");
//    }
//
//    @Test
//    void testFindByTags() {
//        Problem p1 = createProblem("Graph Problem", 100, List.of(ProblemTags.GRAPH_THEORY));
//        Problem p2 = createProblem("DP Problem", 100, List.of(ProblemTags.DP));
//        problemRepository.saveAll(List.of(p1, p2));
//
//        var page = problemRepository.findByTags(List.of(ProblemTags.GRAPH_THEORY), 1, PageRequest.of(0, 10));
//
//        assertThat(page.getContent()).hasSize(1);
//        assertThat(page.getContent().getFirst().getTitle()).isEqualTo("Graph Problem");
//    }
//
//    @Test
//    void testFindByTagsAndRateRange() {
//        Problem p1 = createProblem("Graph Easy", 200, List.of(ProblemTags.GRAPH_THEORY));
//        Problem p2 = createProblem("Graph Hard", 1200, List.of(ProblemTags.GRAPH_THEORY));
//        Problem p3 = createProblem("DP Medium", 500, List.of(ProblemTags.DP));
//        problemRepository.saveAll(List.of(p1, p2, p3));
//
//        var page = problemRepository.findByTagsAndRateRange(
//                List.of(ProblemTags.GRAPH_THEORY),
//                1,
//                100,
//                1000,
//                PageRequest.of(0, 10)
//        );
//
//        assertThat(page.getContent()).hasSize(1);
//        assertThat(page.getContent().getFirst().getTitle()).isEqualTo("Graph Easy");
//    }
//}