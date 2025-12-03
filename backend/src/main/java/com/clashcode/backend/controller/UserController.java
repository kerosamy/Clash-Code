package com.clashcode.backend.controller;

import com.clashcode.backend.dto.ProfileDto;
import com.clashcode.backend.dto.UserSearchResponse;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // Correct constructor
    public UserController(UserService userService) {
        this.userService = userService;
    }


    // Get full user profile
    @GetMapping("/profile/{id}")
    public ResponseEntity<ProfileDto> getProfile(@AuthenticationPrincipal User user) {
        ProfileDto profile = userService.getProfile(user.getId());
        return ResponseEntity.ok(profile);
    }

    // Search users by username
    @GetMapping("/search")
    public ResponseEntity<List<UserSearchResponse>> searchUsers(@RequestParam String username) {
        List<UserSearchResponse> results = userService.searchByUsername(username);
        return ResponseEntity.ok(results);
    }


}
