package com.clashcode.backend.repository;

import com.clashcode.backend.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<UserModel,Integer> {
    UserModel findByEmail(String email);
}
