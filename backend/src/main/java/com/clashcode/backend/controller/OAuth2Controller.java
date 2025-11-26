package com.clashcode.backend.controller;

import com.clashcode.backend.dto.ProfileDto;
import com.clashcode.backend.dto.SignUpCompletionDto;
import com.clashcode.backend.dto.UserSearchResponse;
import com.clashcode.backend.dto.OAuth2Dto;

import java.util.List;

import com.clashcode.backend.service.OAuth2Service;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class OAuth2Controller {
    private final OAuth2Service OAuth2Service;

    public OAuth2Controller(OAuth2Service OAuth2Service) {
        this.OAuth2Service = OAuth2Service;
    }

    @GetMapping("/OAuthCallback")
    public OAuth2Dto handleGoogleOAuth(OAuth2AuthenticationToken authenticationToken) {
        return OAuth2Service.handleOAuth2(authenticationToken);}


    @PostMapping("/GoogleSignUp/completeRegistration")
    public ResponseEntity<OAuth2Dto> completeSignUp(@RequestBody SignUpCompletionDto request, OAuth2AuthenticationToken oauToken) {
        OAuth2Dto user = OAuth2Service.completeSignUp(request, oauToken);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<ProfileDto> getProfile(@PathVariable Long id) {
        ProfileDto profile= OAuth2Service.getProfile(id);
        return ResponseEntity.ok(profile);
    }

        
    @GetMapping("/search")
    public ResponseEntity<List<UserSearchResponse>> searchUsers (@RequestParam String username) {
        List<UserSearchResponse> results = OAuth2Service.searchByUsername(username);
        return ResponseEntity.ok(results);
    }


}
