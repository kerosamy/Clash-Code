package com.clashcode.backend.service;

import com.clashcode.backend.dto.*;
import com.clashcode.backend.enums.ProblemTags;
import com.clashcode.backend.enums.Ranks;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.mapper.UserMapper;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.UserRepository;

import java.util.List;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper = new UserMapper();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDto handleOAuth2(OAuth2AuthenticationToken authenticationToken) {
        OAuth2User oAuth2User = authenticationToken.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        User userExist = userRepository.findByEmail(email);
        if (userExist == null) {
            UserResponseDto dto = new UserResponseDto();
            dto.setEmail(email);
            return dto;
        }
        return userMapper.toUserResponseDto(userExist);
    }

    public UserResponseDto completeSignUp(SignUpCompletionDto request, OAuth2AuthenticationToken oauthToken) {
        String email = oauthToken.getPrincipal().getAttribute("email");
        String username = request.getUsername();
        User userWithSameUsername = userRepository.findByUsername(username);
        if (userWithSameUsername != null) {
            throw new RuntimeException("Username already taken");
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(username);
        User saved_user = userRepository.save(newUser);
        return userMapper.toUserResponseDto(saved_user);
    }

    private StatsDto getStats(User user){
        //TODO
        int solvedProblems = 750;
        int attemptedProblems = 525;
        int matchesPlayed = 330;
        int matchesWon = 230;

        return StatsDto.builder()
                .solvedProblems(solvedProblems)
                .attemptedProblems(attemptedProblems)
                .matchesPlayed(matchesPlayed)
                .matchesWon(matchesWon)
                .build();
    }

    private CategoryDto[] getCategories(User user){
        //TODO
        return new CategoryDto[] {
                CategoryDto.builder()
                        .name(ProblemTags.DP.name())
                        .value(20)
                        .build(),

                CategoryDto.builder()
                        .name(ProblemTags.TWO_POINTERS.name())
                        .value(40)
                        .build(),

                CategoryDto.builder()
                        .name(ProblemTags.BRUTE_FORCE.name())
                        .value(30)
                        .build(),

                CategoryDto.builder()
                        .name(ProblemTags.BFS.name())
                        .value(15)
                        .build(),
        };
    }

    private String getRank(int rate) {
        int index = rate / 300;

        if (index >= Ranks.values().length) {
            index = Ranks.values().length - 1;
        }

        return Ranks.values()[index].name();
    }

    private int getFriendCount(long id){
        //TODO
        return 20;
    }

    public ProfileDto getProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));

        String rank = getRank(user.getCurrentRate());
        int friendCount = getFriendCount(id);
        StatsDto stats = getStats(user);
        CategoryDto[] categories = getCategories(user);

        return ProfileDto.builder()
                .username(user.getUsername())
                .rank(rank)
                .currentRate(user.getCurrentRate())
                .maxRate(user.getMaxRate())
                .friendCount(friendCount)
                .avatarUrl(user.getImgUrl())
                .stats(stats)
                .categories(categories)
                .build();
    }

    public List<UserSearchResponse> searchByUsername(String username) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(username);

        return users.stream()
                .map(user -> {
                    String rank = getRank(user.getCurrentRate());
                    return new UserSearchResponse(
                            user.getUsername(),
                            rank
                    );
                })
                .toList();
    }
}