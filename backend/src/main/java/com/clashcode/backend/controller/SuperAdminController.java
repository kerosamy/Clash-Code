package com.clashcode.backend.controller;

import com.clashcode.backend.dto.UserManagementDto;
import com.clashcode.backend.enums.Roles;
import com.clashcode.backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/super-admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final UserService userService;

    public SuperAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserManagementDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserManagementDto> users = userService.getAllUsers(page, size);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/search")
    public ResponseEntity<Page<UserManagementDto>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserManagementDto> users = userService.searchUsersByUsername(keyword, page, size);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userId}/promote-to-admin")
    public ResponseEntity<?> promoteToAdmin(@PathVariable Long userId) {
        userService.updateUserRole(userId, Roles.ADMIN);
        return ResponseEntity.ok("User promoted to ADMIN");
    }

    @PutMapping("/users/{userId}/demote-to-user")
    public ResponseEntity<?> demoteToUser(@PathVariable Long userId) {
        userService.updateUserRole(userId, Roles.USER);
        return ResponseEntity.ok("User demoted to USER");
    }

    @GetMapping("/users/filter")
    public ResponseEntity<Page<UserManagementDto>> getFilteredUsersByRole(
            @RequestParam String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Roles roleEnum = Roles.valueOf(role);
        Page<UserManagementDto> users = userService.getFilteredUsersByRole(roleEnum, page, size);
        return ResponseEntity.ok(users);
    }
}