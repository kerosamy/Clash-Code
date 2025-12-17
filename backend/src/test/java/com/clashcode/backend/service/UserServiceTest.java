package com.clashcode.backend.service;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.Ranks;
import com.clashcode.backend.enums.Roles;
import com.clashcode.backend.enums.SubmissionStatus;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.mapper.UserMapper;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.*;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private SubmissionRepository submissionRepository;
    @Mock private MatchParticipantRepository matchParticipantRepository;
    @Mock private FriendRepository friendRepository;
    @Mock private UserMapper userMapper;

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

        lenient().when(userMapper.toUserProfile(any(), any(), anyInt(), any(), any(), any()))
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

        lenient().when(userMapper.toUserProfile(any(), any(), anyInt(), any(), any(), any()))
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
        assertEquals("mina", result.get(0).getUsername());
        assertEquals("MASTER", result.get(0).getRank());
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
}