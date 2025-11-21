package com.clashcode.backend.controller;

import com.clashcode.backend.dto.UserDto;
import com.clashcode.backend.service.UserService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/GoogleSignUp")
    public UserDto signUpWithGoogle(OAuth2AuthenticationToken authenticationToken) {
        return userService.handleOAuth2Signup(authenticationToken);
    }


}
