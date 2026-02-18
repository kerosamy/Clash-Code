package com.clashcode.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "test_case_files")
@Data
public class TestCaseFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long problemId;

    @Column(nullable = false)
    private Long testCaseId;

    @Column(nullable = false)
    private String fileType;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String filePath;
}

