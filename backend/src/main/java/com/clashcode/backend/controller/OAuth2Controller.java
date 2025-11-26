package com.clashcode.backend.controller;

import com.clashcode.backend.dto.ProfileDto;
import com.clashcode.backend.dto.SignUpCompletionDto;
import com.clashcode.backend.dto.UserSearchResponse;
import com.clashcode.backend.dto.OAuth2Dto;
import com.clashcode.backend.service.UserService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class OAuth2Controller {
    private final UserService userService;

    public OAuth2Controller(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/OAuthCallback")
    public OAuth2Dto handleGoogleOAuth(OAuth2AuthenticationToken authenticationToken) {
        return userService.handleOAuth2(authenticationToken);
    }


    @PostMapping("/GoogleSignUp/completeRegistration")
    public ResponseEntity<OAuth2Dto> completeSignUp(@RequestBody SignUpCompletionDto request, OAuth2AuthenticationToken oauToken) {
        OAuth2Dto user = userService.completeSignUp(request, oauToken);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<ProfileDto> getProfile(@PathVariable Long id) {
        ProfileDto profile= userService.getProfile(id);
        return ResponseEntity.ok(profile);
    }

        
    @GetMapping("/search")
    public ResponseEntity<List<UserSearchResponse>> searchUsers (@RequestParam String username) {
        List<UserSearchResponse> results = userService.searchByUsername(username);
        return ResponseEntity.ok(results);
    }


}
