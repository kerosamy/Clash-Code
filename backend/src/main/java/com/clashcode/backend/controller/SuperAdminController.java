package com.clashcode.backend.controller;

import com.clashcode.backend.enums.Roles;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/super-admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final UserService userService;

    public SuperAdminController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/users/{userId}/promote-to-admin")
    public ResponseEntity<?> promoteToAdmin(@PathVariable Long userId) {
        User updated = userService.updateUserRole(userId, Roles.ADMIN);
        return ResponseEntity.ok("User promoted to ADMIN");
    }

    @PutMapping("/users/{userId}/demote-to-user")
    public ResponseEntity<?> demoteToUser(@PathVariable Long userId) {
        User updated = userService.updateUserRole(userId, Roles.USER);
        return ResponseEntity.ok("User demoted to USER");
    }

}