package com.clashcode.backend.controller;

import com.clashcode.backend.dto.ProfileDto;
import com.clashcode.backend.dto.UserSearchResponseDto;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile(@AuthenticationPrincipal User user) {
        ProfileDto profile = userService.getProfile(user.getId());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserSearchResponseDto>> searchUsers(@RequestParam String username) {
        List<UserSearchResponseDto> results = userService.searchByUsername(username);
        return ResponseEntity.ok(results);
    }
}
