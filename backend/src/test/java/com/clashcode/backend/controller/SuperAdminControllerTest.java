package com.clashcode.backend.controller;

import com.clashcode.backend.dto.UserManagementDto;
import com.clashcode.backend.enums.Roles;
import com.clashcode.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SuperAdminControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private SuperAdminController superAdminController;

    @Test
    void testGetAllUsers_Success() {
        // Arrange
        UserManagementDto user1 = UserManagementDto.builder()
                .id(1L)
                .username("mina")
                .email("mina@example.com")
                .role("USER")
                .rank("BRONZE")
                .build();

        UserManagementDto user2 = UserManagementDto.builder()
                .id(2L)
                .username("caro")
                .email("caro@example.com")
                .role("ADMIN")
                .rank("DIAMOND")
                .build();

        Page<UserManagementDto> usersPage = new PageImpl<>(List.of(user1, user2));
        when(userService.getAllUsers(0, 20)).thenReturn(usersPage);

        // Act
        ResponseEntity<Page<UserManagementDto>> response = superAdminController.getAllUsers(0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        assertEquals("mina", response.getBody().getContent().get(0).getUsername());
        assertEquals("caro", response.getBody().getContent().get(1).getUsername());
        verify(userService, times(1)).getAllUsers(0, 20);
    }

    @Test
    void testSearchUsers_Success() {
        // Arrange
        UserManagementDto user = UserManagementDto.builder()
                .id(1L)
                .username("mina")
                .email("mina@example.com")
                .role("USER")
                .rank("BRONZE")
                .build();

        Page<UserManagementDto> usersPage = new PageImpl<>(List.of(user));
        when(userService.searchUsersByUsername("mina", 0, 20)).thenReturn(usersPage);

        // Act
        ResponseEntity<Page<UserManagementDto>> response = superAdminController.searchUsers("mina", 0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("mina", response.getBody().getContent().get(0).getUsername());
        verify(userService, times(1)).searchUsersByUsername("mina", 0, 20);
    }

    @Test
    void testPromoteToAdmin_Success() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userService).updateUserRole(userId, Roles.ADMIN);

        // Act
        ResponseEntity<?> response = superAdminController.promoteToAdmin(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User promoted to ADMIN", response.getBody());
        verify(userService, times(1)).updateUserRole(userId, Roles.ADMIN);
    }

    @Test
    void testDemoteToUser_Success() {
        // Arrange
        Long userId = 2L;
        doNothing().when(userService).updateUserRole(userId, Roles.USER);

        // Act
        ResponseEntity<?> response = superAdminController.demoteToUser(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User demoted to USER", response.getBody());
        verify(userService, times(1)).updateUserRole(userId, Roles.USER);
    }

    @Test
    void testGetFilteredUsersByRole_AdminRole_Success() {
        // Arrange
        UserManagementDto admin1 = UserManagementDto.builder()
                .id(1L)
                .username("admin1")
                .email("admin1@example.com")
                .role("ADMIN")
                .rank("MASTER")
                .build();

        UserManagementDto admin2 = UserManagementDto.builder()
                .id(2L)
                .username("admin2")
                .email("admin2@example.com")
                .role("ADMIN")
                .rank("DIAMOND")
                .build();

        Page<UserManagementDto> adminsPage = new PageImpl<>(List.of(admin1, admin2));
        when(userService.getFilteredUsersByRole(Roles.ADMIN, 0, 20)).thenReturn(adminsPage);

        // Act
        ResponseEntity<Page<UserManagementDto>> response = superAdminController.getFilteredUsersByRole("ADMIN", 0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        assertEquals("ADMIN", response.getBody().getContent().get(0).getRole());
        assertEquals("ADMIN", response.getBody().getContent().get(1).getRole());
        verify(userService, times(1)).getFilteredUsersByRole(Roles.ADMIN, 0, 20);
    }

    @Test
    void testGetFilteredUsersByRole_UserRole_Success() {
        // Arrange
        UserManagementDto user = UserManagementDto.builder()
                .id(3L)
                .username("regularUser")
                .email("user@example.com")
                .role("USER")
                .rank("BRONZE")
                .build();

        Page<UserManagementDto> usersPage = new PageImpl<>(List.of(user));
        when(userService.getFilteredUsersByRole(Roles.USER, 0, 20)).thenReturn(usersPage);

        // Act
        ResponseEntity<Page<UserManagementDto>> response = superAdminController.getFilteredUsersByRole("USER", 0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("USER", response.getBody().getContent().get(0).getRole());
        verify(userService, times(1)).getFilteredUsersByRole(Roles.USER, 0, 20);
    }

    @Test
    void testGetAllUsers_WithCustomPagination() {
        // Arrange
        Page<UserManagementDto> usersPage = new PageImpl<>(List.of());
        when(userService.getAllUsers(2, 50)).thenReturn(usersPage);

        // Act
        ResponseEntity<Page<UserManagementDto>> response = superAdminController.getAllUsers(2, 50);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userService, times(1)).getAllUsers(2, 50);
    }

    @Test
    void testGetAllUsers_EmptyResult() {
        // Arrange
        Page<UserManagementDto> emptyPage = new PageImpl<>(List.of());
        when(userService.getAllUsers(0, 20)).thenReturn(emptyPage);

        // Act
        ResponseEntity<Page<UserManagementDto>> response = superAdminController.getAllUsers(0, 20);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
        verify(userService, times(1)).getAllUsers(0, 20);
    }
}