package com.clashcode.backend.service;

import com.clashcode.backend.dto.LoginUserDto;
import com.clashcode.backend.dto.RegisterUserDto;
import com.clashcode.backend.dto.SignUpCompletionDto;
import com.clashcode.backend.enums.Roles;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.UserRepository;
import com.clashcode.backend.mapper.UserMapper;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper = new UserMapper();

    public AuthService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto input) {

        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        if (userRepository.findByUsername(input.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        String password = passwordEncoder.encode(input.getPassword());
        User user = userMapper.toUser(input, password);

        return userRepository.save(user);
    }


    public String getRecoveryQuestion(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (user.getRecoveryQuestion() == null) {
            throw new RuntimeException("User does not have a recovery question");
        }
        return user.getRecoveryQuestion().name();
    }

    public boolean verifyRecoveryAnswer(String email, String answer) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found."));

        if (user.getRecoveryAnswer() == null || !user.getRecoveryAnswer().equalsIgnoreCase(answer.trim())) {
            throw new RuntimeException("Incorrect recovery answer.");
        }
        return true;
    }

    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    // ---------------------------
    //  EMAIL / PASSWORD LOGIN
    // ---------------------------
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



    // ---------------------------
    //  GOOGLE OAUTH CALLBACK
    // ---------------------------
    public User handleGoogleOAuth(OAuth2AuthenticationToken authenticationToken) {
        OAuth2User oauth = authenticationToken.getPrincipal();
        String email = oauth.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    return newUser;
                });

        user.setRole(Roles.USER);
        return user;
    }

    public User completeGoogleSignUp(SignUpCompletionDto dto, OAuth2AuthenticationToken token) {
        String email = token.getPrincipal().getAttribute("email");

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(dto.getUsername());
        newUser.setCurrentRate(0);
        newUser.setMaxRate(0);
        newUser.setRole(Roles.USER);

        return userRepository.save(newUser);
    }

}
