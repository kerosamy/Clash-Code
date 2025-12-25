package com.clashcode.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "problem_ai_review")
public class ProblemAIReview {
    @Id
    @Column(name = "problem_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column(name = "problem_hash", nullable = false)
    private String problemHash;

    @Column(name = "review_json", columnDefinition = "TEXT")
    private String reviewJSON;
}
