package com.clashcode.backend.controller;

import com.clashcode.backend.dto.SignUpCompletionDto;
import com.clashcode.backend.dto.UserResponseDto;
import com.clashcode.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/OAuthCallback")
    public UserResponseDto handleGoogleOAuth(OAuth2AuthenticationToken authenticationToken) {
        return userService.handleOAuth2(authenticationToken);
    }


    @PostMapping("/GoogleSignUp/completeRegistration")
    public ResponseEntity<UserResponseDto> completeSignUp(@RequestBody SignUpCompletionDto request, OAuth2AuthenticationToken oauToken) {
        UserResponseDto user = userService.completeSignUp(request, oauToken);
        return ResponseEntity.ok(user);
    }
}