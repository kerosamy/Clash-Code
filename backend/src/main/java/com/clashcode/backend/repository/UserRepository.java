package com.clashcode.backend.repository;

import com.clashcode.backend.dto.UserSearchDto;
import com.clashcode.backend.enums.Roles;
import com.clashcode.backend.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    List<User>findByUsernameContainingIgnoreCase(String username);
    Page<User> findAllByRoleNot(Roles role, Pageable pageable);
    Page<User> findAllByRole(Roles role, Pageable pageable);

    @Query("""
                SELECT u, f
                FROM User u
                LEFT JOIN Friend f
                  ON ((f.sender.id = :loggedInUserId AND f.receiver = u)
                  OR (f.receiver.id = :loggedInUserId AND f.sender = u))
                WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))
           """)
    List<Object[]> searchByUsernameWithStatus(
            @Param("loggedInUserId") Long loggedInUserId,
            @Param("username") String username
    );

    // Add to UserRepository.java
    Page<User> findAllByOrderByCurrentRateDesc(Pageable pageable);
    
}
