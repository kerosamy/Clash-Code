package com.clashcode.backend.service;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.enums.Ranks;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ProfileDto getProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));

        return ProfileDto.builder()
                .username(user.getUsername())
                .rank(getRank(user.getCurrentRate()))
                .currentRate(user.getCurrentRate())
                .maxRate(user.getMaxRate())
                .friendCount(getFriendCount(id))
                .avatarUrl(user.getImgUrl())
                .stats(getStats(user))
                .categories(getCategories(user))
                .build();
    }

    public List<UserSearchResponse> searchByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .map(user -> new UserSearchResponse(
                        user.getUsername(),
                        getRank(user.getCurrentRate())
                )).toList();
    }

    // ----------- PRIVATE HELPERS --------------

    private StatsDto getStats(User user) {
        return StatsDto.builder()
                .solvedProblems(750)
                .attemptedProblems(525)
                .matchesPlayed(330)
                .matchesWon(230)
                .build();
    }

    private CategoryDto[] getCategories(User user) {
        return new CategoryDto[]{
                new CategoryDto(ProblemTags.DP.name(), 20),
                new CategoryDto(ProblemTags.TWO_POINTERS.name(), 40),
                new CategoryDto(ProblemTags.BRUTE_FORCE.name(), 30),
                new CategoryDto(ProblemTags.BFS.name(), 15),
        };
    }

    private String getRank(int rate) {
        int index = rate / 300;
        if (index >= Ranks.values().length)
            index = Ranks.values().length - 1;
        return Ranks.values()[index].name();
    }

    private int getFriendCount(long id) {
        return 20; // Placeholder
    }
}
