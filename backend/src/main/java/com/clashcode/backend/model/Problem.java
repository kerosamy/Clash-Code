package com.clashcode.backend.model;

import com.clashcode.backend.enums.Judge0Language;
import com.clashcode.backend.enums.ProblemRate;
import com.clashcode.backend.enums.ProblemStatus;
import com.clashcode.backend.enums.ProblemTags;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "problem")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Builder.Default
    @Column
    private Long submissionsCount = 0L ;

    @Column(nullable = false , length = 100)
    private String title ;

    @Column(nullable = false , length = 1000)
    private String inputFormat ;

    @Column(nullable = false , length = 1000)
    private String outputFormat ;

    @Column(nullable = false , length = 2000)
    private String statement ;

    @Column(length = 1000)
    private String notes ;

    @Column(nullable = false)
    private Integer timeLimit ;

    @Column(nullable = false)
    private Integer memoryLimit ;

    @Column(nullable = false)
    private String mainSolution ;

    @Builder.Default // remove after testing
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProblemStatus problemStatus = ProblemStatus.PENDING_APPROVAL ;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Judge0Language judge0Language ;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProblemRate rate ;

    @ElementCollection(targetClass = ProblemTags.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "problem_topics",
            joinColumns = @JoinColumn(name = "problem_id")
    )
    @Column(name = "tags")
    private List<ProblemTags> tags = new ArrayList<>();

    @OneToMany(mappedBy = "problem" , cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    private List<TestCase> testCases = new ArrayList<>();
}
