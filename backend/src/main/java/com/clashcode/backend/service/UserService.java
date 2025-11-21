package com.clashcode.backend.service;

import com.clashcode.backend.dto.UserDto;
import com.clashcode.backend.model.UserModel;
import com.clashcode.backend.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    public UserDto handleOAuth2Signup(OAuth2AuthenticationToken authenticationToken) {
        OAuth2User oAuth2User = authenticationToken.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        UserModel userExist = userRepo.findByEmail(email);
        UserDto dto;
        if (userExist == null) {
            return saveUser(email);
        }
        dto = UserDto.builder()
                .id(userExist.getId())
                .username(userExist.getUsername())
                .build();
        log.info("Logged in user: {}", dto);
        return dto;
    }

    public UserDto saveUser(String email) {
        UserModel user = UserModel.builder()
                         .email(email)
                         .username(email)
                         .password(null)
                         .build();
        UserModel savedUser = this.userRepo.save(user);
        log.info("User saved successfully!");
        return UserDto.builder()
                .id(savedUser.getId())
                .username((savedUser.getEmail()))
                .build();
    }
}
