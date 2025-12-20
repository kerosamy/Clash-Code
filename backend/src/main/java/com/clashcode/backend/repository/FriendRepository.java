package com.clashcode.backend.repository;

import com.clashcode.backend.enums.FriendRequestStatus;
import com.clashcode.backend.model.Friend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("""
        SELECT f FROM Friend f WHERE
        (f.sender.id = :user1Id AND f.receiver.id = :user2Id) OR
        (f.sender.id = :user2Id AND f.receiver.id = :user1Id)
    """)
    Optional<Friend> findRelationshipBetweenUsers(@Param("user1Id") Long user1Id,
                                                  @Param("user2Id") Long user2Id);

    Optional<Friend> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    @Query("SELECT f FROM Friend f WHERE :userId IN (f.sender.id, f.receiver.id) AND f.status = 'ACCEPTED'")
    Page<Friend> findAllFriendsByUserId(@Param("userId") Long userId, Pageable pageable);

    // Incoming requests
    Page<Friend> findByReceiverIdAndStatus(Long receiverId, FriendRequestStatus status, Pageable pageable);

    // Sent requests
    Page<Friend> findBySenderIdAndStatus(Long senderId, FriendRequestStatus status, Pageable pageable);

    @Query("""
                SELECT COUNT(f)
                FROM Friend f
                WHERE (f.sender.id = :userId OR f.receiver.id = :userId) AND f.status = :status
           """)
    int countFriendsByUserId(
            @Param("userId") Long userId,
            @Param("status") FriendRequestStatus status
    );
}
