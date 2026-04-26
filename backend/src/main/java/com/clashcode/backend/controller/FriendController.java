package com.clashcode.backend.controller;

import com.clashcode.backend.dto.FriendDto;
import com.clashcode.backend.enums.FriendStatus;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.FriendService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friends")
@PreAuthorize("hasRole('USER')")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/send/{username}")
    public ResponseEntity<Void> sendFriendRequest(@PathVariable String username,
                                                  @AuthenticationPrincipal User sender) {
        friendService.sendFriendRequest(sender, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{username}")
    public ResponseEntity<Void> acceptFriendRequest(@PathVariable String username,
                                                    @AuthenticationPrincipal User user) {
        friendService.acceptFriendRequest(user, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/reject/{username}")
    public ResponseEntity<Void> rejectFriendRequest(@PathVariable String username,
                                                    @AuthenticationPrincipal User user) {
        friendService.rejectFriendRequest(user, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{username}")
    public ResponseEntity<Void> removeFriend(@PathVariable String username,
                                             @AuthenticationPrincipal User user) {
        friendService.removeFriend(user, username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{username}")
    public ResponseEntity<FriendStatus> getFriendshipStatus(@PathVariable String username,
                                                            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(friendService.getStatus(user, username));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<FriendDto>> getFriendsList(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(friendService.getFriendsList(user, pageable));
    }

    @GetMapping("/sent-requests")
    public ResponseEntity<Page<FriendDto>> getSentFriendRequests(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(friendService.getSentFriendRequests(user, pageable));
    }

    @GetMapping("/received-requests")
    public ResponseEntity<Page<FriendDto>> getReceivedFriendRequests(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(friendService.getReceivedFriendRequests(user, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FriendDto>> searchFriends(
            @AuthenticationPrincipal User user,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(friendService.searchFriendsByUsername(user, query.trim(), pageable));
    }

}
