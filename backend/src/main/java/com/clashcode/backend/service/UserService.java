package com.clashcode.backend.service;

import com.clashcode.backend.dto.SignUpCompletionDto;
import com.clashcode.backend.dto.UserDto;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.UserRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto handleOAuth2Signup(OAuth2AuthenticationToken authenticationToken) {
        OAuth2User oAuth2User = authenticationToken.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        User userExist = userRepository.findByEmail(email);
        if (userExist == null) {
            UserDto dto = new UserDto();
            dto.setEmail(email);
            return dto;
        }
        return convertToDto(userExist);
    }

    public UserDto completeSignUp(SignUpCompletionDto request, OAuth2AuthenticationToken oauthToken) {
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
        return convertToDto(saved_user);
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
