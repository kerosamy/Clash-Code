package com.clashcode.backend.service;

import com.clashcode.backend.dto.ProfileDto;
import com.clashcode.backend.dto.UserManagementDto;
import com.clashcode.backend.dto.UserSearchResponseDto;
import com.clashcode.backend.enums.Roles;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetProfile_AssemblesCorrectProfile() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("mina");
        user.setCurrentRate(1140);
        user.setMaxRate(1200);
        user.setImgUrl("https://example.com/avatar.png");

        // Act
        ProfileDto profile = userService.getProfile(user);

        // Assert
        assertEquals("mina", profile.getUsername());
        assertEquals("DIAMOND", profile.getRank()); // depends on getRank logic
        assertEquals(1140, profile.getCurrentRate());
        assertEquals(1200, profile.getMaxRate());
        assertNotNull(profile.getStats());
        assertNotNull(profile.getCategories());
    }

    @Test
    void testGetUserProfile_Success() {
        // Arrange
        User user = new User();
        user.setId(2L);
        user.setUsername("caro");
        user.setCurrentRate(1400);
        user.setMaxRate(1500);
        user.setImgUrl("https://example.com/caro.png");

        when(userRepository.findByUsername("caro")).thenReturn(Optional.of(user));

        // Act
        ProfileDto profile = userService.getUserProfile("caro");

        // Assert
        assertEquals("caro", profile.getUsername());
        assertEquals(1400, profile.getCurrentRate());
        assertEquals(1500, profile.getMaxRate());
        assertNotNull(profile.getStats());
        assertNotNull(profile.getCategories());
    }

    @Test
    void testGetUserProfile_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserProfile("unknown"));
    }

    @Test
    void testSearchByUsername_Found() {
        // Arrange
        User user1 = new User();
        user1.setUsername("caro");
        user1.setCurrentRate(1200);

        User user2 = new User();
        user2.setUsername("caroline");
        user2.setCurrentRate(1500);

        when(userRepository.findByUsernameContainingIgnoreCase("car"))
                .thenReturn(List.of(user1, user2));

        // Act
        List<UserSearchResponseDto> results = userService.searchByUsername("car");

        // Assert
        assertEquals(2, results.size());
        assertEquals("caro", results.get(0).getUsername());
        assertEquals("MASTER", results.get(0).getRank());
        assertEquals("caroline", results.get(1).getUsername());
        assertEquals("CHAMPION", results.get(1).getRank());
    }

    @Test
    void testSearchByUsername_NoMatch() {
        when(userRepository.findByUsernameContainingIgnoreCase("mo"))
                .thenReturn(List.of());

        List<UserSearchResponseDto> results = userService.searchByUsername("mo");

        assertTrue(results.isEmpty());
    }

    // tests for User Management feature

    @Test
    void testGetAllUsers_ExcludesSuperAdmin() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("mina");
        user1.setEmail("mina@example.com");
        user1.setRole(Roles.USER);
        user1.setCurrentRate(500);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("caro");
        user2.setEmail("caro@example.com");
        user2.setRole(Roles.ADMIN);
        user2.setCurrentRate(1200);

        PageRequest pageRequest = PageRequest.of(0, 20);
        Page<User> usersPage = new PageImpl<>(List.of(user1, user2), pageRequest, 2);

        when(userRepository.findAllByRoleNot(Roles.SUPER_ADMIN, pageRequest)).thenReturn(usersPage);

        // Act
        Page<UserManagementDto> result = userService.getAllUsers(0, 20);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals("mina", result.getContent().get(0).getUsername());
        assertEquals("caro", result.getContent().get(1).getUsername());
        verify(userRepository, times(1)).findAllByRoleNot(Roles.SUPER_ADMIN, pageRequest);
    }

    @Test
    void testSearchUsersByUsername_WithPagination() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("mina");
        user1.setEmail("mina@example.com");
        user1.setRole(Roles.USER);
        user1.setCurrentRate(800);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("minato");
        user2.setEmail("minato@example.com");
        user2.setRole(Roles.ADMIN);
        user2.setCurrentRate(1100);

        when(userRepository.findByUsernameContainingIgnoreCase("min"))
                .thenReturn(List.of(user1, user2));

        // Act
        Page<UserManagementDto> result = userService.searchUsersByUsername("min", 0, 20);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals("mina", result.getContent().get(0).getUsername());
        assertEquals("minato", result.getContent().get(1).getUsername());
        assertEquals("mina@example.com", result.getContent().get(0).getEmail());
    }

    @Test
    void testUpdateUserRole_PromoteToAdmin() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("mina");
        user.setRole(Roles.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.updateUserRole(1L, Roles.ADMIN);

        // Assert
        assertEquals(Roles.ADMIN, user.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserRole_DemoteToUser() {
        // Arrange
        User user = new User();
        user.setId(2L);
        user.setUsername("caro");
        user.setRole(Roles.ADMIN);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.updateUserRole(2L, Roles.USER);

        // Assert
        assertEquals(Roles.USER, user.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserRole_CannotModifySuperAdmin() {
        // Arrange
        User superAdmin = new User();
        superAdmin.setId(1L);
        superAdmin.setUsername("superadmin");
        superAdmin.setRole(Roles.SUPER_ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(superAdmin));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateUserRole(1L, Roles.USER));
        assertEquals("Cannot modify SUPER_ADMIN role", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserRole_UserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> userService.updateUserRole(999L, Roles.ADMIN));
    }

    @Test
    void testGetFilteredUsersByRole_AdminOnly() {
        // Arrange
        User admin1 = new User();
        admin1.setId(1L);
        admin1.setUsername("admin1");
        admin1.setEmail("admin1@example.com");
        admin1.setRole(Roles.ADMIN);
        admin1.setCurrentRate(1200);

        User admin2 = new User();
        admin2.setId(2L);
        admin2.setUsername("admin2");
        admin2.setEmail("admin2@example.com");
        admin2.setRole(Roles.ADMIN);
        admin2.setCurrentRate(1500);

        PageRequest pageRequest = PageRequest.of(0, 20);
        Page<User> adminsPage = new PageImpl<>(List.of(admin1, admin2), pageRequest, 2);

        when(userRepository.findAllByRole(Roles.ADMIN, pageRequest)).thenReturn(adminsPage);

        // Act
        Page<UserManagementDto> result = userService.getFilteredUsersByRole(Roles.ADMIN, 0, 20);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals("ADMIN", result.getContent().get(0).getRole());
        assertEquals("ADMIN", result.getContent().get(1).getRole());
        verify(userRepository, times(1)).findAllByRole(Roles.ADMIN, pageRequest);
    }

    @Test
    void testGetFilteredUsersByRole_UserOnly() {
        // Arrange
        User user1 = new User();
        user1.setId(3L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setRole(Roles.USER);
        user1.setCurrentRate(600);

        PageRequest pageRequest = PageRequest.of(0, 20);
        Page<User> usersPage = new PageImpl<>(List.of(user1), pageRequest, 1);

        when(userRepository.findAllByRole(Roles.USER, pageRequest)).thenReturn(usersPage);

        // Act
        Page<UserManagementDto> result = userService.getFilteredUsersByRole(Roles.USER, 0, 20);

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals("USER", result.getContent().getFirst().getRole());
        assertEquals("user1", result.getContent().getFirst().getUsername());
        verify(userRepository, times(1)).findAllByRole(Roles.USER, pageRequest);
    }

}