package com.clashcode.backend.model;

import com.clashcode.backend.enums.RecoveryQuestion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 255)
    private String password = "";

    @Column(nullable = false)
    private Boolean isAdmin = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String imgUrl;

    @Column(nullable = false)
    private Integer maxRate = 0;

    @Column(nullable = false)
    private Integer currentRate = 0;

    @Enumerated(EnumType.STRING)
    private RecoveryQuestion recoveryQuestion;

    private String recoveryAnswer;

}
