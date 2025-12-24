package com.clashcode.backend.service;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.*;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.mapper.FriendStatusMapper;
import com.clashcode.backend.mapper.UserMapper;
import com.clashcode.backend.model.Friend;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.FriendRepository;
import com.clashcode.backend.repository.MatchParticipantRepository;
import com.clashcode.backend.repository.SubmissionRepository;
import com.clashcode.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final SubmissionRepository submissionRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final ImageFileStorageService imageFileStorageService;
    private final UserMapper userMapper = new UserMapper();
    private final FriendStatusMapper friendStatusMapper = new FriendStatusMapper();
    private final RedisService redisService;
    private static final int RATING_PER_RANK = 300;
    private static final Ranks[] RANKS = Ranks.values();

    public UserService(UserRepository userRepository, FriendRepository friendRepository, SubmissionRepository submissionRepository, MatchParticipantRepository matchParticipantRepository, ImageFileStorageService imageFileStorageService) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.submissionRepository = submissionRepository;
        this.matchParticipantRepository = matchParticipantRepository;
        this.imageFileStorageService = imageFileStorageService;
        this.redisService = redisService;
    }

    public List<UserSearchResponseDto> searchByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .map(user -> new UserSearchResponseDto(
                        user.getUsername(),
                        getRank(user.getCurrentRate()),
                        getUserStatus(user.getId())
                )).toList();
    }

    public List<UserSearchDto> searchByUsername(String username, User loggedInUser) {
            List<Object[]> results = userRepository.searchByUsernameWithStatus(loggedInUser.getId(), username);

            return results.stream().map(row -> {
                User u = (User) row[0];
                Friend f = (Friend) row[1];
                FriendStatus status = friendStatusMapper.map(u,f);
                return new UserSearchDto(u.getUsername(), u.getCurrentRate(), status);
            }).toList();
    }

    private StatsDto getStats(long userId) {
        int solvedProblems = submissionRepository.countDistinctSolvedProblems(userId, SubmissionStatus.ACCEPTED);
        int attemptedProblems = submissionRepository.countDistinctAttemptedProblems(userId);
        int matchesPlayed = matchParticipantRepository.countMatches(userId);
        int matchesWon = matchParticipantRepository.countWonMatches(userId);

        return new StatsDto(solvedProblems, attemptedProblems, matchesPlayed, matchesWon);
    }

    private CategoryDto[] getCategories(long userId) {
        List<Object[]> results = submissionRepository.countProblemsByCategory(userId, SubmissionStatus.ACCEPTED);

        return results.stream()
                .map(r -> new CategoryDto(r[0].toString(), ((Number) r[1]).intValue()))
                .toArray(CategoryDto[]::new);
    }

    private int getFriendCount(long userId) {
        return friendRepository.countFriendsByUserId(userId, FriendRequestStatus.ACCEPTED);
    }
    
    public String getRank(int rate) {
        int index = rate / RATING_PER_RANK;
        if (index >= Ranks.values().length)
            index = Ranks.values().length - 1;
        return RANKS[index].name();
    }

    public ProfileDto getProfile(User user) {
        String rank = getRank(user.getCurrentRate());
        int friendCount = getFriendCount(user.getId());
        StatsDto stats = getStats(user.getId());
        CategoryDto[] categories = getCategories(user.getId());

        // Convert stored filename to full URL
        String imageUrl = buildImageUrl(user.getImgUrl());

        return userMapper.toUserProfile(user,
                rank,
                friendCount,
                stats,
                categories,
                imageUrl,
                getUserStatus(user.getId())
        );
    }

    public ProfileDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username " + username));

        return getProfile(user);
    }

    public void updateUserRole(Long userId, Roles newRole) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user.getRole() == Roles.SUPER_ADMIN) {
            throw new RuntimeException("Cannot modify SUPER_ADMIN role");
        }
        user.setRole(newRole);
        userRepository.save(user);
    }

    public Page<UserManagementDto> getAllUsers(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return userRepository.findAllByRoleNot(Roles.SUPER_ADMIN, pageRequest)
                .map(user -> userMapper.toUserManagementDto(user, getRank(user.getCurrentRate())));
    }

    public Page<UserManagementDto> searchUsersByUsername(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(keyword);

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), users.size());

        List<UserManagementDto> userDtos = users.subList(start, end).stream()
                .map(user -> userMapper.toUserManagementDto(user, getRank(user.getCurrentRate())))
                .toList();

        return new PageImpl<>(userDtos, pageRequest, users.size());
    }

    public Page<UserManagementDto> getFilteredUsersByRole(Roles role, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return userRepository.findAllByRole(role, pageRequest)
                .map(user -> userMapper.toUserManagementDto(user, getRank(user.getCurrentRate())));
    }

    public String updateProfileImage(User user, MultipartFile file) {
        // Delete old image if exists
        if (user.getImgUrl() != null && !user.getImgUrl().isEmpty()) {
            imageFileStorageService.deleteFile(user.getImgUrl());
        }

        // Store new image
        String fileName = imageFileStorageService.storeFile(file, user.getUsername());

        // Update user record with filename
        user.setImgUrl(fileName);
        userRepository.save(user);

        // Return full URL
        return buildImageUrl(fileName);
    }

    public void deleteProfileImage(User user) {
        if (user.getImgUrl() != null && !user.getImgUrl().isEmpty()) {
            imageFileStorageService.deleteFile(user.getImgUrl());
            user.setImgUrl(null);
            userRepository.save(user);
        }
    }

    public String buildImageUrl(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        return fileName;
    }

    public Page<LeaderBoardDto> getLeaderboard(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return userRepository.findAllByOrderByCurrentRateDesc(pageRequest)
                .map(userMapper::toLeaderboardDto);
    }

    public UserStatus getUserStatus(Long userId){
        if(!isOnline(userId)){
            return UserStatus.OFFLINE;
        }
        return UserStatus.ONLINE;
        //to do check if in match
    }

    public void markOnline(User user) {
        try {
            redisService.addUserToRedis(user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isOnline(Long userId) {
        try {
            return redisService.searchUserFromRedis(userId);
        } catch (Exception e) {
            return false;
        }
    }
}
