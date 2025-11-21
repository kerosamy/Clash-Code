package com.clashcode.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "test_case")
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(nullable = false)
    private String input ;

    @Column(nullable = false)
    private boolean visible ;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem ;

}
