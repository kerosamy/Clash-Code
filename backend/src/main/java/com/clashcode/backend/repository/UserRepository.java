package com.clashcode.backend.repository;

import com.clashcode.backend.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
    User findByUsername(String username);
    List<User>findByUsernameContainingIgnoreCase(String username);
}
