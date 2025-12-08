package com.clashcode.backend.model;

import com.clashcode.backend.enums.LanguageVersion;
import com.clashcode.backend.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "submission")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    @Column(nullable = false)
    private String code;

    @Column
    @Enumerated(EnumType.STRING)
    private LanguageVersion languageVersion;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

    @Column
    private Integer memoryTaken;

    @Column
    private Integer timeTaken;

    @Column
    Integer passedTestCases;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id" , nullable = false)
    private Problem problem;

}
