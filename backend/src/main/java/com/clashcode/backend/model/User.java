package com.clashcode.backend.model;

import com.clashcode.backend.enums.RecoveryQuestion;
import com.clashcode.backend.enums.Roles;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Roles role = Roles.USER;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String imgUrl;

    @Column(nullable = false)
    @Builder.Default
    private Integer maxRate = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentRate = 0;

    @Enumerated(EnumType.STRING)
    private RecoveryQuestion recoveryQuestion;

    private String recoveryAnswer;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == Roles.SUPER_ADMIN) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        } else if (role == Roles.ADMIN) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        } else { 
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

}
