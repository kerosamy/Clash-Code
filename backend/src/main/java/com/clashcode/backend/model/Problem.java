package com.clashcode.backend.model;
import com.clashcode.backend.enums.ProblemRate;
import com.clashcode.backend.enums.ProblemStatus;
import com.clashcode.backend.enums.ProblemTags;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    private Long id;

    @Builder.Default
    @Column(nullable = false)
    private Long submissionsCount = 0L;

    @Column(nullable = false , length = 100)
    private String title;

    @Column(nullable = false , columnDefinition = "TEXT")
    private String inputFormat;

    @Column(nullable = false , columnDefinition = "TEXT")
    private String outputFormat;

    @Column(nullable = false , columnDefinition = "TEXT")
    private String statement;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Min(250) //ms
    @Max(10000)
    @Column(nullable = false)
    private Integer timeLimit;

    @Min(4) //MB
    @Max(512)
    @Column(nullable = false)
    private Integer memoryLimit;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProblemStatus problemStatus = ProblemStatus.PENDING_APPROVAL;

    @Embedded
    private Solution solution;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProblemRate rate;

    @ElementCollection(targetClass = ProblemTags.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "problem_topics",
            joinColumns = @JoinColumn(name = "problem_id")
    )
    @Builder.Default
    @Column(name = "tags" , nullable = false)
    private List<ProblemTags> tags = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "problem" , cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    private List<TestCase> testCases = new ArrayList<>();
}
