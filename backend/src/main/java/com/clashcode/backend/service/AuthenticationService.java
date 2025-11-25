package com.clashcode.backend.service;

import  com.clashcode.backend.dto.LoginUserDto;
import  com.clashcode.backend.dto.RegisterUserDto;
import com.clashcode.backend.enums.RecoveryQuestion;
import  com.clashcode.backend.model.User;
import  com.clashcode.backend.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto input) {

        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        if (userRepository.findByUsername(input.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        User user = User.builder()
                .username(input.getUsername())
                .email(input.getEmail())
                .password(passwordEncoder.encode(input.getPassword()))
                .recoveryQuestion(input.getRecoveryQuestion() != null
                        ? RecoveryQuestion.valueOf(input.getRecoveryQuestion())
                        : null)
                .recoveryAnswer(input.getRecoveryAnswer())
                .isAdmin(false)
                .maxRate(0)
                .currentRate(0)
                .build();

        return userRepository.save(user);
    }


    public User authenticate(LoginUserDto input) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
