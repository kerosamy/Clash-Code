package com.clashcode.backend.service;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.enums.Ranks;
import com.clashcode.backend.enums.Roles;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.mapper.UserMapper;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper = new UserMapper();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserSearchResponseDto> searchByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .map(user -> new UserSearchResponseDto(
                        user.getUsername(),
                        getRank(user.getCurrentRate())
                )).toList();
    }

    private StatsDto getStats(User user) {
        //TODO
        int solvedProblems = 750;
        int attemptedProblems = 525;
        int matchesPlayed = 330;
        int matchesWon = 230;

        return new StatsDto(solvedProblems, attemptedProblems, matchesPlayed, matchesWon);
    }

    private CategoryDto[] getCategories(User user) {
        //TODO
        return new CategoryDto[]{
                new CategoryDto(ProblemTags.DP.name(), 20),
                new CategoryDto(ProblemTags.TWO_POINTERS.name(), 40),
                new CategoryDto(ProblemTags.BRUTE_FORCE.name(), 30),
                new CategoryDto(ProblemTags.BFS.name(), 15),
        };
    }

    private int getFriendCount(User user) {
        //TODO
        return 20;
    }

    private String getRank(int rate) {
        int index = rate / 300;
        if (index >= Ranks.values().length)
            index = Ranks.values().length - 1;
        return Ranks.values()[index].name();
    }

    public ProfileDto getProfile(User user) {
        String rank = getRank(user.getCurrentRate());
        int friendCount = getFriendCount(user);
        StatsDto stats = getStats(user);
        CategoryDto[] categories = getCategories(user);
        return userMapper.toUserProfile(user, rank, friendCount, stats, categories);
    }

    public ProfileDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username " + username));

        return getProfile(user);
    }

    public User updateUserRole(Long userId, Roles newRole) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setRole(newRole);
        return userRepository.save(user);
    }

}
