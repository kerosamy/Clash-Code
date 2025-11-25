package com.clashcode.backend.controller;

import com.clashcode.backend.dto.SignUpCompletionDto;
import com.clashcode.backend.dto.UserDto;
import com.clashcode.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins =  "http://localhost:5173", allowCredentials = "true")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/GoogleSignUp")
    public UserDto signUpWithGoogle(OAuth2AuthenticationToken authenticationToken) {
        return userService.handleOAuth2Signup(authenticationToken);
    }

    @PostMapping("/GoogleSignUp/completeRegistration")
    public ResponseEntity<UserDto> completeSignUp(@RequestBody SignUpCompletionDto request, OAuth2AuthenticationToken oauToken) {
        UserDto user = userService.completeSignUp(request, oauToken);
        return ResponseEntity.ok(user);
    }
}
