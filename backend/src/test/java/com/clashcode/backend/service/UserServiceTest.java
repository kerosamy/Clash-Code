package com.clashcode.backend.service;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.*;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.mapper.FriendStatusMapper;
import com.clashcode.backend.mapper.UserMapper;
import com.clashcode.backend.model.Friend;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

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
        User user = new User();
        user.setId(2L);
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
    void test_searchUsersByUsername_Success() {
        // Arrange
        User u1 = new User();
        u1.setUsername("alice");
        u1.setCurrentRate(1200);

        User u2 = new User();
        u2.setUsername("alicia");
        u2.setCurrentRate(1500);

        when(userRepository.findByUsernameContainingIgnoreCase("ali"))
                .thenReturn(List.of(u1, u2));

        // Act
        Page<UserManagementDto> result = userService.searchUsersByUsername("ali", 0, 10);

        // Assert
        assertEquals(2, result.getTotalElements());
        assertEquals("alice", result.getContent().get(0).getUsername());
        assertEquals("alicia", result.getContent().get(1).getUsername());
    }

    @Test
    void test_searchUsersByUsername_WithPagination() {
        // Arrange
        User u1 = new User();
        u1.setUsername("user1");
        u1.setCurrentRate(1000);

        User u2 = new User();
        u2.setUsername("user2");
        u2.setCurrentRate(1100);

        User u3 = new User();
        u3.setUsername("user3");
        u3.setCurrentRate(1200);

        when(userRepository.findByUsernameContainingIgnoreCase("user"))
                .thenReturn(List.of(u1, u2, u3));

        // Act - Get first page with size 2
        Page<UserManagementDto> result = userService.searchUsersByUsername("user", 0, 2);

        // Assert
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("user1", result.getContent().get(0).getUsername());
        assertEquals("user2", result.getContent().get(1).getUsername());
    }

    @Test
    void test_searchUsersByUsername_NoResults() {
        // Arrange
        when(userRepository.findByUsernameContainingIgnoreCase("xyz"))
                .thenReturn(List.of());

        // Act
        Page<UserManagementDto> result = userService.searchUsersByUsername("xyz", 0, 10);

        // Assert
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void test_getFilteredUsersByRole_Success() {
        // Arrange
        User admin = new User();
        admin.setUsername("admin1");
        admin.setRole(Roles.ADMIN);
        admin.setCurrentRate(1500);

        Page<User> page = new PageImpl<>(List.of(admin), PageRequest.of(0, 10), 1);

        when(userRepository.findAllByRole(eq(Roles.ADMIN), any()))
                .thenReturn(page);

        // Act
        Page<UserManagementDto> result = userService.getFilteredUsersByRole(Roles.ADMIN, 0, 10);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("admin1", result.getContent().getFirst().getUsername());
        verify(userRepository).findAllByRole(eq(Roles.ADMIN), any());
    }

    @Test
    void test_deleteProfileImage_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setImgUrl("existing_image.jpg");

        // Act
        userService.deleteProfileImage(user);

        // Assert
        assertNull(user.getImgUrl());
        verify(imageFileStorageService).deleteFile("existing_image.jpg");
        verify(userRepository).save(user);
    }

    @Test
    void test_deleteProfileImage_NoImageToDelete() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setImgUrl(null);

        // Act
        userService.deleteProfileImage(user);

        // Assert
        assertNull(user.getImgUrl());
        verify(imageFileStorageService, never()).deleteFile(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void test_deleteProfileImage_EmptyImageUrl() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setImgUrl("");

        // Act
        userService.deleteProfileImage(user);

        // Assert
        assertEquals("", user.getImgUrl());
        verify(imageFileStorageService, never()).deleteFile(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void test_buildImageUrl_WithFileName_ReturnsFileName() {
        // Act
        String result = userService.buildImageUrl("user_avatar.jpg");

        // Assert
        assertEquals("user_avatar.jpg", result);
    }

    @Test
    void test_buildImageUrl_WithNullFileName_ReturnsNull() {
        // Act
        String result = userService.buildImageUrl(null);

        // Assert
        assertNull(result);
    }

    @Test
    void test_buildImageUrl_WithEmptyFileName_ReturnsNull() {
        // Act
        String result = userService.buildImageUrl("");

        // Assert
        assertNull(result);
    }
    @Test
    void updateProfileImage_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setImgUrl(null);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.jpg",
                "image/jpeg",
                "image content".getBytes()
        );

        when(imageFileStorageService.storeFile(file, "testuser"))
                .thenReturn("https://cloudinary.com/avatar.jpg");

        String result = userService.updateProfileImage(user, file);

        assertEquals("https://cloudinary.com/avatar.jpg", result);
        assertEquals("https://cloudinary.com/avatar.jpg", user.getImgUrl());
        verify(imageFileStorageService).storeFile(file, "testuser");
        verify(userRepository).save(user);
    }

    @Test
    void updateProfileImage_WithExistingImage_DeletesOld() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setImgUrl("old_image.jpg");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "new_avatar.jpg",
                "image/jpeg",
                "new image content".getBytes()
        );

        when(imageFileStorageService.storeFile(file, "testuser"))
                .thenReturn("https://cloudinary.com/new_avatar.jpg");

        String result = userService.updateProfileImage(user, file);

        assertEquals("https://cloudinary.com/new_avatar.jpg", result);
        verify(imageFileStorageService).deleteFile("old_image.jpg");
        verify(imageFileStorageService).storeFile(file, "testuser");
        verify(userRepository).save(user);
    }

    @Test
    void searchByUsername_WithLoggedInUser_Success() {
        User loggedInUser = new User();
        loggedInUser.setId(1L);

        User foundUser = new User();
        foundUser.setId(2L);
        foundUser.setUsername("testuser");
        foundUser.setCurrentRate(1200);

        Friend friend = Friend.builder()
                .sender(loggedInUser)
                .receiver(foundUser)
                .status(FriendRequestStatus.ACCEPTED)
                .build();

        Object[] row = {foundUser, friend};
        List<Object[]> rows = List.<Object[]>of(row);

        when(userRepository.searchByUsernameWithStatus(1L, "test"))
                .thenReturn(rows);



        FriendStatusMapper friendStatusMapper = mock(FriendStatusMapper.class);
        when(friendStatusMapper.map(foundUser, friend)).thenReturn(FriendStatus.FRIENDS);
        ReflectionTestUtils.setField(userService, "friendStatusMapper", friendStatusMapper);

        List<UserSearchDto> result = userService.searchByUsername("test", loggedInUser);

        assertEquals(1, result.size());
        assertEquals("testuser", result.getFirst().getUsername());
        assertEquals(1200, result.getFirst().getCurrentRate());
    }

    @Test
    void getRank_BoundaryValues() {
        assertEquals("BRONZE", userService.getRank(0));
        assertEquals("BRONZE", userService.getRank(299));
        assertEquals("SILVER", userService.getRank(300));
        assertEquals("SILVER", userService.getRank(599));
        assertEquals("GOLD", userService.getRank(600));
        assertEquals("GOLD", userService.getRank(899));
        assertEquals("DIAMOND", userService.getRank(900));
        assertEquals("DIAMOND", userService.getRank(1199));
        assertEquals("MASTER", userService.getRank(1200));
        assertEquals("MASTER", userService.getRank(1499));
        assertEquals("CHAMPION", userService.getRank(1500));
        assertEquals("CHAMPION", userService.getRank(1799));
        assertEquals("LEGEND", userService.getRank(1800));
        assertEquals("LEGEND", userService.getRank(10000));
    }
}