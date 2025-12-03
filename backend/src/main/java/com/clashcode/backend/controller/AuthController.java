package com.clashcode.backend.controller;

import com.clashcode.backend.dto.LoginUserDto;
import com.clashcode.backend.dto.OAuth2Dto;
import com.clashcode.backend.dto.RegisterUserDto;
import com.clashcode.backend.dto.SignUpCompletionDto;
import com.clashcode.backend.exception.UnauthorizedException;
import com.clashcode.backend.model.User;
import com.clashcode.backend.responses.LoginResponse;
import com.clashcode.backend.service.AuthService;
import com.clashcode.backend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;

    public AuthController(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authService.signup(registerUserDto);
        return buildLoginResponse(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authService.authenticate(loginUserDto);
        return buildLoginResponse(authenticatedUser);
    }

    // GOOGLE LOGIN
    @GetMapping("/OAuthCallback")
    public ResponseEntity<LoginResponse> handleGoogleOAuth(OAuth2AuthenticationToken token) {
        User user = authService.handleGoogleOAuth(token); // <-- now returns User
        return buildLoginResponse(user);
    }

    // GOOGLE SIGNUP COMPLETION
    @PostMapping("/GoogleSignUp/completeRegistration")
    public ResponseEntity<LoginResponse> completeSignUp(
            @RequestBody SignUpCompletionDto dto,
            OAuth2AuthenticationToken token
    ) {
        User createdUser = authService.completeGoogleSignUp(dto, token); // <-- returns User
        return buildLoginResponse(createdUser);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal User user) {
        if (user == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        return ResponseEntity.ok(user); // returns minimal user info (id, email, username)
    }

    private ResponseEntity<LoginResponse> buildLoginResponse(User user) {
        String jwtToken = jwtService.generateToken(user);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}
