package com.clashcode.backend.model;

import com.clashcode.backend.enums.FriendRequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "friend",
    uniqueConstraints = @UniqueConstraint(
            columnNames = {"sender_id", "receiver_id"}
    ))
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id" , nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id" , nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
