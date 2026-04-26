package com.clashcode.backend.controller;

import com.clashcode.backend.dto.LeaderBoardDto;
import com.clashcode.backend.dto.ProfileDto;
import com.clashcode.backend.dto.UserSearchDto;
import com.clashcode.backend.dto.UserSearchResponseDto;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('USER')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getMyProfile(@AuthenticationPrincipal User user) {
        ProfileDto profile = userService.getProfile(user);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<ProfileDto> getUserProfile(@PathVariable String username) {
        ProfileDto profile = userService.getUserProfile(username);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserSearchResponseDto>> searchUsers(@RequestParam String username) {
        List<UserSearchResponseDto> results = userService.searchByUsername(username);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/profile/image")
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = userService.updateProfileImage(user, file);
        System.out.print(imageUrl);
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }

    @DeleteMapping("/profile/image")
    public ResponseEntity<Void> deleteProfileImage(@AuthenticationPrincipal User user) {
        userService.deleteProfileImage(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-with-friend-status")
    public ResponseEntity<List<UserSearchDto>> searchUsersWithFriendStatus(
            @RequestParam String username,
            @AuthenticationPrincipal User user
    ) {
        List<UserSearchDto> results = userService.searchByUsername(username, user);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<org.springframework.data.domain.Page<LeaderBoardDto>> getLeaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getLeaderboard(page, size));
    }
    @PostMapping("/status/online")
    public ResponseEntity<Void> updateOnlineStatus(@AuthenticationPrincipal User user) {
        userService.markOnline(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/status/in-match")
    public ResponseEntity<Void> updateInMatchStatus(@AuthenticationPrincipal User user) {
        userService.markInMatch(user);
        return ResponseEntity.noContent().build();
    }
}