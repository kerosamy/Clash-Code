package com.clashcode.backend.repository;

import com.clashcode.backend.enums.LanguageVersion;
import com.clashcode.backend.enums.ProblemStatus;
import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.model.Problem;
import com.clashcode.backend.model.Solution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class ProblemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProblemRepository problemRepository;

    @BeforeEach
    void setUp() {
        // clear
        problemRepository.deleteAll();
        entityManager.clear();

        // create a sample solution object
        Solution solution1 = Solution.builder()
                .solutionCode("// Sample solution code for Two Sum")
                .languageVersion(LanguageVersion.JAVA_OPENJDK_13)
                .build();

        Solution solution2 = Solution.builder()
                .solutionCode("// Binary search solution")
                .languageVersion(LanguageVersion.JAVA_OPENJDK_13)
                .build();

        Solution solution3 = Solution.builder()
                .solutionCode("// Merge sort solution")
                .languageVersion(LanguageVersion.JAVA_OPENJDK_13)
                .build();

        Solution solution4 = Solution.builder()
                .solutionCode("// DFS solution")
                .languageVersion(LanguageVersion.JAVA_OPENJDK_13)
                .build();

        // create test problems with different tags and rates
        Problem problem1 = Problem.builder()
                .title("Two Sum")
                .statement("Find two numbers that add up to target")
                .inputFormat("Array of integers and target")
                .outputFormat("Indices of two numbers")
                .notes("Use hash map for O(n) solution")
                .timeLimit(1000)
                .memoryLimit(256)
                .rate(1000)
                .submissionsCount(0L)
                .problemStatus(ProblemStatus.PENDING_APPROVAL)
                .solution(solution1)
                .tags(new ArrayList<>(Arrays.asList(ProblemTags.DATA_STRUCTURES, ProblemTags.HASHING)))
                .testCases(new ArrayList<>())
                .build();

        Problem problem2 = Problem.builder()
                .title("Binary Search")
                .statement("Search for target in sorted array")
                .inputFormat("Sorted array and target")
                .outputFormat("Index of target or -1")
                .notes("Classic binary search")
                .timeLimit(1000)
                .memoryLimit(128)
                .rate(1200)
                .submissionsCount(0L)
                .problemStatus(ProblemStatus.PENDING_APPROVAL)
                .solution(solution2)
                .tags(new ArrayList<>(Arrays.asList(ProblemTags.DATA_STRUCTURES, ProblemTags.BINARY_SEARCH)))
                .testCases(new ArrayList<>())
                .build();

        Problem problem3 = Problem.builder()
                .title("Merge Sort")
                .statement("Sort array using merge sort")
                .inputFormat("Unsorted array")
                .outputFormat("Sorted array")
                .notes("Divide and conquer algorithm")
                .timeLimit(2000)
                .memoryLimit(512)
                .rate(1400)
                .submissionsCount(0L)
                .problemStatus(ProblemStatus.PENDING_APPROVAL)
                .solution(solution3)
                .tags(new ArrayList<>(Arrays.asList(ProblemTags.SORTING, ProblemTags.DATA_STRUCTURES)))
                .testCases(new ArrayList<>())
                .build();

        Problem problem4 = Problem.builder()
                .title("Graph Traversal")
                .statement("Traverse graph using DFS")
                .inputFormat("Adjacency list")
                .outputFormat("Traversal order")
                .notes("Use recursion or stack")
                .timeLimit(3000)
                .memoryLimit(256)
                .rate(1000)
                .submissionsCount(0L)
                .problemStatus(ProblemStatus.PENDING_APPROVAL)
                .solution(solution4)
                .tags(new ArrayList<>(Arrays.asList(ProblemTags.GRAPH_THEORY, ProblemTags.DFS_AND_SIMILAR)))
                .testCases(new ArrayList<>())
                .build();

        problem1 = entityManager.persist(problem1);
        problem2 = entityManager.persist(problem2);
        problem3 = entityManager.persist(problem3);
        problem4 = entityManager.persist(problem4);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findByRate_ShouldReturnProblemsWithSpecificRate() {
        // Given
        Integer rate = 1000;
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Problem> result = problemRepository.findByRate(rate, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Problem::getTitle)
                .containsExactlyInAnyOrder("Two Sum", "Graph Traversal");
        assertThat(result.getContent())
                .allMatch(p -> p.getRate() == 1000);
    }

    @Test
    void findByRate_WithNonExistingRate_ShouldReturnEmptyPage() {
        // Given
        Integer rate = 5000;
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Problem> result = problemRepository.findByRate(rate, pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByTags_WithSingleTag_ShouldReturnMatchingProblems() {
        // Given
        List<ProblemTags> tags = List.of(ProblemTags.DATA_STRUCTURES);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Problem> result = problemRepository.findByTags(tags, pageable);

        // Then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent())
                .extracting(Problem::getTitle)
                .containsExactlyInAnyOrder("Two Sum", "Binary Search", "Merge Sort");
    }

    @Test
    void findByTags_WithMultipleTags_ShouldReturnProblemsMatchingAnyTag() {
        // Given
        List<ProblemTags> tags = Arrays.asList(ProblemTags.DATA_STRUCTURES, ProblemTags.GRAPH_THEORY);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Problem> result = problemRepository.findByTags(tags, pageable);

        // Then
        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getContent())
                .extracting(Problem::getTitle)
                .containsExactlyInAnyOrder("Two Sum", "Binary Search", "Merge Sort", "Graph Traversal");
    }

    @Test
    void findByTags_WithNonExistingTag_ShouldReturnEmptyPage() {
        // Given
        List<ProblemTags> tags = List.of(ProblemTags.DP);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Problem> result = problemRepository.findByTags(tags, pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByTagsAndRate_WithBothParameters_ShouldReturnMatchingProblems() {
        // Given
        List<ProblemTags> tags = List.of(ProblemTags.DATA_STRUCTURES);
        Integer rate = 1000;
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Problem> result = problemRepository.findByTagsAndRate(tags, rate, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getTitle()).isEqualTo("Two Sum");
        assertThat(result.getContent().getFirst().getRate()).isEqualTo(1000);
        assertThat(result.getContent().getFirst().getTags()).contains(ProblemTags.DATA_STRUCTURES);
    }

    @Test
    void findByTagsAndRate_WithNoMatch_ShouldReturnEmptyPage() {
        // Given
        List<ProblemTags> tags = List.of(ProblemTags.GRAPH_THEORY);
        Integer rate = 1200;
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Problem> result = problemRepository.findByTagsAndRate(tags, rate, pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByRate_WithPagination_ShouldReturnCorrectPage() {
        // Given
        Integer rate = 1000;
        Pageable pageable = PageRequest.of(0, 1);

        // When
        Page<Problem> result = problemRepository.findByRate(rate, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    void findByTags_WithPagination_ShouldReturnCorrectPage() {
        // Given
        List<ProblemTags> tags = List.of(ProblemTags.DATA_STRUCTURES);
        Pageable pageable = PageRequest.of(0, 1);

        // When
        Page<Problem> result = problemRepository.findByTags(tags, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(3);
    }

    @Test
    void findAll_ShouldReturnAllProblems() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Problem> result = problemRepository.findAll(pageable);

        // Then
        assertThat(result.getContent()).hasSize(4);
    }

    @Test
    void findByTags_ShouldReturnDistinctProblems() {
        // Given
        List<ProblemTags> tags = Arrays.asList(ProblemTags.DATA_STRUCTURES, ProblemTags.HASHING);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Problem> result = problemRepository.findByTags(tags, pageable);

        // Then
        assertThat(result.getContent()).hasSize(3);
        long distinctCount = result.getContent().stream()
                .map(Problem::getId)
                .distinct()
                .count();
        assertThat(distinctCount).isEqualTo(result.getContent().size());
    }
}