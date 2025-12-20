package com.clashcode.backend.service;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.*;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.mapper.FriendStatusMapper;
import com.clashcode.backend.mapper.UserMapper;
import com.clashcode.backend.model.Friend;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import com.clashcode.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private SubmissionRepository submissionRepository;
    @Mock private MatchParticipantRepository matchParticipantRepository;
    @Mock private FriendRepository friendRepository;
    @Mock private UserMapper userMapper;
    @Mock private FriendStatusMapper friendStatusMapper;
    @Mock private RedisService redisService;

    @Mock
    private ImageFileStorageService imageFileStorageService;

    @InjectMocks
    private UserService userService;

    @Test
    void test_getProfile_shouldAssembleStatsCategoriesFriendsAndRank() {
        User user = new User();
        user.setId(3L);
        user.setUsername("kero");
        user.setCurrentRate(1230);
        user.setMaxRate(1500);
        user.setImgUrl("avatar.png");

        when(submissionRepository.countDistinctSolvedProblems(eq(3L), eq(SubmissionStatus.ACCEPTED)))
                .thenReturn(5);
        when(submissionRepository.countDistinctAttemptedProblems(3L))
                .thenReturn(7);
        when(matchParticipantRepository.countMatches(3L))
                .thenReturn(3);
        when(matchParticipantRepository.countWonMatches(3L))
                .thenReturn(2);
        when(friendRepository.countFriendsByUserId(eq(3L), any()))
                .thenReturn(5);

        when(submissionRepository.countProblemsByCategory(eq(3L), eq(SubmissionStatus.ACCEPTED)))
                .thenReturn(List.of(
                        new Object[]{"DP", 2L},
                        new Object[]{"GRAPH_Theory", 3L}
                ));

        ProfileDto expected = ProfileDto.builder()
                .username("kero")
                .rank("MASTER")
                .currentRate(1230)
                .maxRate(1500)
                .friendCount(5)
                .stats(new StatsDto(5, 7, 3, 2))
                .categories(new CategoryDto[]{
                        new CategoryDto("DP", 2),
                        new CategoryDto("GRAPH_Theory", 3)
                })
                .avatarUrl("http://localhost/images/avatar.png")
                .build();

        lenient().when(userMapper.toUserProfile(any(), any(), anyInt(), any(), any(), any(), any()))
                .thenReturn(expected);

        ProfileDto result = userService.getProfile(user);

        assertEquals("kero", result.getUsername());
        assertEquals("MASTER", result.getRank());
        assertEquals(5, result.getFriendCount());
        assertEquals(5, result.getStats().getSolvedProblems());
        assertEquals(2, result.getCategories().length);
    }

    @Test
    void test_getProfile_shouldCapRankWhenRateExceedsMax() {
        User user = new User();
        user.setId(10L);
        user.setUsername("overflow");
        user.setCurrentRate(100_000);

        when(submissionRepository.countDistinctSolvedProblems(anyLong(), any()))
                .thenReturn(0);
        when(submissionRepository.countDistinctAttemptedProblems(anyLong()))
                .thenReturn(0);
        when(matchParticipantRepository.countMatches(anyLong()))
                .thenReturn(0);
        when(matchParticipantRepository.countWonMatches(anyLong()))
                .thenReturn(0);
        when(friendRepository.countFriendsByUserId(anyLong(), any()))
                .thenReturn(0);
        when(submissionRepository.countProblemsByCategory(anyLong(), any()))
                .thenReturn(List.of());

        lenient().when(userMapper.toUserProfile(any(), any(), anyInt(), any(), any(), any(), any()))
                .thenAnswer(invocation ->
                        ProfileDto.builder()
                                .rank(invocation.getArgument(1))
                                .build()
                );

        ProfileDto profile = userService.getProfile(user);

        assertEquals(
                Ranks.values()[Ranks.values().length - 1].name(),
                profile.getRank()
        );
    }

    @Test
    void test_getUserProfile_success() {
    @DisplayName("searchByUsername returns mapped results")
    void searchByUsername_found() {
        User user = new User();
        user.setUsername("mina");
        user.setCurrentRate(1200);

        when(userRepository.findByUsernameContainingIgnoreCase("min"))
                .thenReturn(List.of(user));

        List<UserSearchResponseDto> results = userService.searchByUsername("min");

        assertEquals(1, results.size());
        assertEquals("mina", results.get(0).getUsername());
        assertEquals("MASTER", results.get(0).getRank());
    }

    @Test
    @DisplayName("getProfile assembles correct profile")
    void getProfile_success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("mina");
        user.setCurrentRate(1140);
        user.setMaxRate(1200);
        user.setImgUrl("avatar.png");

        ProfileDto profile = userService.getProfile(user);

        assertEquals("mina", profile.getUsername());
        assertEquals("DIAMOND", profile.getRank());
        assertTrue(profile.getFriendCount() > 0);
        assertNotNull(profile.getStats());
        assertNotNull(profile.getCategories());
        assertTrue(profile.getAvatarUrl().contains("avatar.png"));
    }

    @Test
    @DisplayName("getUserProfile success")
    void getUserProfile_success() {
        User user = new User();
        user.setUsername("caro");
        user.setCurrentRate(1400);
        user.setMaxRate(1500);

        when(userRepository.findByUsername("caro"))
                .thenReturn(Optional.of(user));

        ProfileDto profile = userService.getUserProfile("caro");

        assertEquals("caro", profile.getUsername());
    }

    @Test
    void test_getUserProfile_userNotFound() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserProfile("unknown"));
    }

    @Test
    void test_searchByUsername_found() {
        User u1 = new User();
        u1.setUsername("mina");
        u1.setCurrentRate(1200);

        User u2 = new User();
        u2.setUsername("minato");
        u2.setCurrentRate(1500);

        when(userRepository.findByUsernameContainingIgnoreCase("min"))
                .thenReturn(List.of(u1, u2));

        List<UserSearchResponseDto> result =
                userService.searchByUsername("min");

        assertEquals(2, result.size());
        assertEquals("mina", result.getFirst().getUsername());
        assertEquals("MASTER", result.getFirst().getRank());
    }

    @Test
    void test_searchByUsername_noResults() {
        when(userRepository.findByUsernameContainingIgnoreCase(any()))
                .thenReturn(List.of());

        assertTrue(userService.searchByUsername("x").isEmpty());
    }

    @Test
    void test_updateUserRole_success() {
        assertEquals(1400, profile.getCurrentRate());
    }

    @Test
    @DisplayName("getUserProfile throws when not found")
    void getUserProfile_notFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserProfile("unknown"));
    }

    @Test
    @DisplayName("updateUserRole changes role")
    void updateUserRole_success() {
        User user = new User();
        user.setId(1L);
        user.setRole(Roles.USER);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(user);

        userService.updateUserRole(1L, Roles.ADMIN);

        assertEquals(Roles.ADMIN, user.getRole());
    }

    @Test
    void test_updateUserRole_cannotModifySuperAdmin() {
        User superAdmin = new User();
        superAdmin.setId(99L);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("updateUserRole throws for SUPER_ADMIN")
    void updateUserRole_superAdmin() {
        User superAdmin = new User();
        superAdmin.setId(1L);
        superAdmin.setRole(Roles.SUPER_ADMIN);

        when(userRepository.findById(99L))
                .thenReturn(Optional.of(superAdmin));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateUserRole(99L, Roles.USER));

        assertEquals("Cannot modify SUPER_ADMIN role", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void test_updateUserRole_userNotFound() {
        when(userRepository.findById(404L))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUserRole(404L, Roles.ADMIN));
    }

    @Test
    void test_getAllUsers_excludesSuperAdmin() {
        User user = new User();
        user.setUsername("mina");

        Page<User> page =
                new PageImpl<>(List.of(user), PageRequest.of(0, 10), 1);

        when(userRepository.findAllByRoleNot(eq(Roles.SUPER_ADMIN), any()))
                .thenReturn(page);

        Page<UserManagementDto> result =
                userService.getAllUsers(0, 10);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void test_searchByUsername_withLoggedInUser_noResults() {
        User loggedInUser = new User();
        loggedInUser.setId(5L);

        when(userRepository.searchByUsernameWithStatus(5L, "xyz"))
                .thenReturn(Collections.emptyList());

        List<UserSearchDto> result = userService.searchByUsername("xyz", loggedInUser);

        assertTrue(result.isEmpty());
        verify(userRepository).searchByUsernameWithStatus(5L, "xyz");
        verify(friendStatusMapper, never()).map(any(), any());
    }

    @Test
    @DisplayName("getLeaderboard - Should return sorted and mapped page")
    void test_getLeaderboard_shouldReturnSortedMappedPage() {
        // 1. ISOLATED INJECTION: Only affects this test run
        // We manually swap the final internal 'userMapper' with our mock
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);

        // 2. Arrange Data
        User u1 = new User();
        u1.setUsername("pro");
        u1.setCurrentRate(2500);
        Page<User> mockUserPage = new PageImpl<>(List.of(u1), PageRequest.of(0, 20), 1);

        LeaderBoardDto mappedDto = new LeaderBoardDto();
        mappedDto.setUsername("pro");
        mappedDto.setCurrentRate(2500);

        // 3. Mock Behavior
        when(userRepository.findAllByOrderByCurrentRateDesc(any(PageRequest.class)))
                .thenReturn(mockUserPage);

        when(userMapper.toLeaderboardDto(u1)).thenReturn(mappedDto);

        // 4. Act
        Page<LeaderBoardDto> result = userService.getLeaderboard(0, 20);

        // 5. Assert & Verify
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(userMapper).toLeaderboardDto(u1);
    }
    @Test
    void test_getUserStatus_WhenUserIsOffline_ShouldReturnOffline() {
        // Arrange
        Long userId = 123L;
        when(redisService.searchUserFromRedis(userId)).thenReturn(false);

        // Act
        UserStatus result = userService.getUserStatus(userId);

        // Assert
        assertEquals(UserStatus.OFFLINE, result);
        verify(redisService).searchUserFromRedis(userId);
        verify(redisService, never()).getUserStatus(userId);
    }

    @Test
    void test_getUserStatus_WhenUserIsOnline_ShouldReturnOnline() {
        // Arrange
        Long userId = 123L;
        when(redisService.searchUserFromRedis(userId)).thenReturn(true);
        when(redisService.getUserStatus(userId)).thenReturn("online");

        // Act
        UserStatus result = userService.getUserStatus(userId);

        // Assert
        assertEquals(UserStatus.ONLINE, result);
        verify(redisService).searchUserFromRedis(userId);
        verify(redisService).getUserStatus(userId);
    }

    @Test
    void test_getUserStatus_WhenUserIsInMatch_ShouldReturnInMatch() {
        // Arrange
        Long userId = 456L;
        when(redisService.searchUserFromRedis(userId)).thenReturn(true);
        when(redisService.getUserStatus(userId)).thenReturn("in-match");

        // Act
        UserStatus result = userService.getUserStatus(userId);

        // Assert
        assertEquals(UserStatus.IN_MATCH, result);
        verify(redisService).searchUserFromRedis(userId);
        verify(redisService).getUserStatus(userId);
    }

    @Test
    void test_getUserStatus_WhenStatusIsUnknown_ShouldReturnOnline() {
        // Arrange
        Long userId = 789L;
        when(redisService.searchUserFromRedis(userId)).thenReturn(true);
        when(redisService.getUserStatus(userId)).thenReturn("some-random-status");

        // Act
        UserStatus result = userService.getUserStatus(userId);

        // Assert
        assertEquals(UserStatus.ONLINE, result);
        verify(redisService).searchUserFromRedis(userId);
        verify(redisService).getUserStatus(userId);
    }

    @Test
    void test_getUserStatus_WhenStatusIsNull_ShouldReturnOnline() {
        // Arrange
        Long userId = 999L;
        when(redisService.searchUserFromRedis(userId)).thenReturn(true);
        when(redisService.getUserStatus(userId)).thenReturn(null);

        // Act
        UserStatus result = userService.getUserStatus(userId);

        // Assert
        assertEquals(UserStatus.ONLINE, result);
        verify(redisService).searchUserFromRedis(userId);
        verify(redisService).getUserStatus(userId);
    }

    @Test
    void test_markOnline_ShouldAddUserToRedisWithOnlineStatus() {
        // Arrange
        User user = new User();
        user.setId(123L);
        user.setUsername("testUser");

        // Act
        userService.markOnline(user);

        // Assert
        verify(redisService).addUserToRedis(123L, "online");
    }

    @Test
    void test_markOnline_WhenRedisThrowsException_ShouldCatchAndNotThrow() {
        // Arrange
        User user = new User();
        user.setId(456L);
        doThrow(new RuntimeException("Redis connection failed"))
                .when(redisService).addUserToRedis(456L, "online");

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> userService.markOnline(user));
        verify(redisService).addUserToRedis(456L, "online");
    }

    @Test
    void test_markInMatch_ShouldAddUserToRedisWithInMatchStatus() {
        // Arrange
        User user = new User();
        user.setId(789L);
        user.setUsername("playerInMatch");

        // Act
        userService.markInMatch(user);

        // Assert
        verify(redisService).addUserToRedis(789L, "in-match");
    }

    @Test
    void test_markInMatch_WhenRedisThrowsException_ShouldCatchAndNotThrow() {
        // Arrange
        User user = new User();
        user.setId(999L);
        doThrow(new RuntimeException("Redis error"))
                .when(redisService).addUserToRedis(999L, "in-match");

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> userService.markInMatch(user));
        verify(redisService).addUserToRedis(999L, "in-match");
    }

    @Test
    void test_isOnline_WhenUserIsOnline_ShouldReturnTrue() {
        // Arrange
        Long userId = 123L;
        when(redisService.searchUserFromRedis(userId)).thenReturn(true);

        // Act
        boolean result = userService.isOnline(userId);

        // Assert
        assertTrue(result);
        verify(redisService).searchUserFromRedis(userId);
    }

    @Test
    void test_isOnline_WhenUserIsOffline_ShouldReturnFalse() {
        // Arrange
        Long userId = 456L;
        when(redisService.searchUserFromRedis(userId)).thenReturn(false);

        // Act
        boolean result = userService.isOnline(userId);

        // Assert
        assertFalse(result);
        verify(redisService).searchUserFromRedis(userId);
    }

    @Test
    void test_isOnline_WhenRedisThrowsException_ShouldReturnFalse() {
        // Arrange
        Long userId = 789L;
        when(redisService.searchUserFromRedis(userId))
                .thenThrow(new RuntimeException("Redis connection lost"));

        // Act
        boolean result = userService.isOnline(userId);

        // Assert
        assertFalse(result);
        verify(redisService).searchUserFromRedis(userId);
    }

    @Test
    void test_markOnline_ThenCheckStatus_ShouldBeOnline() {
        // Arrange
        User user = new User();
        user.setId(100L);
        when(redisService.searchUserFromRedis(100L)).thenReturn(true);
        when(redisService.getUserStatus(100L)).thenReturn("online");

        // Act
        userService.markOnline(user);
        UserStatus status = userService.getUserStatus(100L);

        // Assert
        assertEquals(UserStatus.ONLINE, status);
        verify(redisService).addUserToRedis(100L, "online");
        verify(redisService).getUserStatus(100L);
    }

    @Test
    void test_markInMatch_ThenCheckStatus_ShouldBeInMatch() {
        // Arrange
        User user = new User();
        user.setId(200L);
        when(redisService.searchUserFromRedis(200L)).thenReturn(true);
        when(redisService.getUserStatus(200L)).thenReturn("in-match");

        // Act
        userService.markInMatch(user);
        UserStatus status = userService.getUserStatus(200L);

        // Assert
        assertEquals(UserStatus.IN_MATCH, status);
        verify(redisService).addUserToRedis(200L, "in-match");
        verify(redisService).getUserStatus(200L);
    }

    @Test
    void test_statusTransition_OnlineToInMatchToOffline() {
        // Arrange
        User user = new User();
        user.setId(300L);

        // Online
        when(redisService.searchUserFromRedis(300L)).thenReturn(true);
        when(redisService.getUserStatus(300L)).thenReturn("online");
        userService.markOnline(user);
        assertEquals(UserStatus.ONLINE, userService.getUserStatus(300L));

        // In Match
        when(redisService.getUserStatus(300L)).thenReturn("in-match");
        userService.markInMatch(user);
        assertEquals(UserStatus.IN_MATCH, userService.getUserStatus(300L));

        // Offline (TTL expired)
        when(redisService.searchUserFromRedis(300L)).thenReturn(false);
        assertEquals(UserStatus.OFFLINE, userService.getUserStatus(300L));

        // Verify
        verify(redisService).addUserToRedis(300L, "online");
        verify(redisService).addUserToRedis(300L, "in-match");
        verify(redisService, atLeast(3)).searchUserFromRedis(300L);
    }
    

    @Test
    @DisplayName("updateUserRole throws when user not found")
    void updateUserRole_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.updateUserRole(99L, Roles.ADMIN));
    }

    @Test
    @DisplayName("getAllUsers returns mapped page")
    void getAllUsers_success() {
        User user = new User();
        user.setUsername("mina");
        user.setCurrentRate(1200);

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user), pageRequest, 1);

        when(userRepository.findAllByRoleNot(Roles.SUPER_ADMIN, pageRequest)).thenReturn(page);

        Page<UserManagementDto> result = userService.getAllUsers(0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals("mina", result.getContent().get(0).getUsername());
    }

    @Test
    @DisplayName("searchUsersByUsername returns paged results")
    void searchUsersByUsername_success() {
        User user1 = new User();
        user1.setUsername("mina");
        user1.setCurrentRate(800);

        User user2 = new User();
        user2.setUsername("minato");
        user2.setCurrentRate(1100);

        when(userRepository.findByUsernameContainingIgnoreCase("min"))
                .thenReturn(List.of(user1, user2));

        Page<UserManagementDto> result = userService.searchUsersByUsername("min", 0, 10);

        assertEquals(2, result.getTotalElements());
        assertEquals("mina", result.getContent().get(0).getUsername());
    }

    @Test
    @DisplayName("getFilteredUsersByRole returns mapped page")
    void getFilteredUsersByRole_success() {
        User user = new User();
        user.setUsername("admin");
        user.setCurrentRate(1200);

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user), pageRequest, 1);

        when(userRepository.findAllByRole(Roles.ADMIN, pageRequest)).thenReturn(page);

        Page<UserManagementDto> result = userService.getFilteredUsersByRole(Roles.ADMIN, 0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals("admin", result.getContent().get(0).getUsername());
    }

    @Test
    @DisplayName("updateProfileImage replaces old image and returns URL")
    void updateProfileImage_withOldImage() {
        User user = new User();
        user.setUsername("mina");
        user.setImgUrl("old.png");

        MockMultipartFile file = new MockMultipartFile("file", "avatar.png",
                "image/png", "dummy".getBytes());

        when(imageFileStorageService.storeFile(file, "mina")).thenReturn("new.png");
        when(userRepository.save(any(User.class))).thenReturn(user);

        String url = userService.updateProfileImage(user, file);

        verify(imageFileStorageService).deleteFile("old.png");
        verify(userRepository).save(user);
        assertTrue(url.contains("new.png"));
    }

    @Test
    @DisplayName("updateProfileImage with no old image")
    void updateProfileImage_noOldImage() {
        User user = new User();
        user.setUsername("mina");

        MockMultipartFile file = new MockMultipartFile("file", "avatar.png",
                "image/png", "dummy".getBytes());

        when(imageFileStorageService.storeFile(file, "mina")).thenReturn("new.png");
        when(userRepository.save(any(User.class))).thenReturn(user);

        String url = userService.updateProfileImage(user, file);

        verify(imageFileStorageService, never()).deleteFile(any());
        verify(userRepository).save(user);
        assertTrue(url.contains("new.png"));
    }

    @Test
    @DisplayName("deleteProfileImage removes existing image")
    void deleteProfileImage_withImage() {
        User user = new User();
        user.setImgUrl("old.png");

        userService.deleteProfileImage(user);

        verify(imageFileStorageService).deleteFile("old.png");
        verify(userRepository).save(user);
        assertNull(user.getImgUrl());
    }

    @Test
    @DisplayName("deleteProfileImage does nothing when no image")
    void deleteProfileImage_noImage() {
        User user = new User();
        user.setImgUrl(null);

        userService.deleteProfileImage(user);

        verify(imageFileStorageService, never()).deleteFile(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("getRank returns correct rank for low, mid, and overflow values")
    void getRank_variousRates() throws Exception {
        UserService service = new UserService(userRepository, imageFileStorageService);

        // Low value
        String rankLow = service.getProfile(new User() {{
            setUsername("low");
            setCurrentRate(100);
        }}).getRank();
        assertEquals("BRONZE", rankLow);

        // Mid value
        String rankMid = service.getProfile(new User() {{
            setUsername("mid");
            setCurrentRate(900);
        }}).getRank();
        assertEquals("DIAMOND", rankMid);

        // Overflow value (beyond enum length)
        String rankHigh = service.getProfile(new User() {{
            setUsername("high");
            setCurrentRate(9999);
        }}).getRank();
        assertEquals("LEGEND", rankHigh); // last enum value
    }

    @Test
    @DisplayName("buildImageUrl returns null for null or empty filename")
    void buildImageUrl_nullOrEmpty() {
        UserService service = new UserService(userRepository, imageFileStorageService);

        // null filename
        String resultNull = service.updateProfileImage(new User(), new MockMultipartFile("file", "avatar.png",
                "image/png", "dummy".getBytes()));
        // since user has no username, storeFile will likely throw, but we can directly test private helper
        String urlNull = service.buildImageUrl(null);
        assertNull(urlNull);

        // empty filename
        String urlEmpty = service.buildImageUrl("");
        assertNull(urlEmpty);
    }

    @Test
    @DisplayName("buildImageUrl returns full URL for valid filename")
    void buildImageUrl_valid() {
        UserService service = new UserService(userRepository, imageFileStorageService);
        String url = service.buildImageUrl("avatar.png");
        assertTrue(url.contains("/files/profile-images/avatar.png"));
    }
}
