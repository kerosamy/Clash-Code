package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.UserDto;
import com.clashcode.backend.model.Friend;
import com.clashcode.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class FriendMapper {

    private final FriendStatusMapper friendStatusMapper;

    public FriendMapper(FriendStatusMapper friendStatusMapper) {
        this.friendStatusMapper = friendStatusMapper;
    }

    public UserDto toUserDto(Friend friendship, User currentUser) {
        User otherUser = friendship.getSender().getId().equals(currentUser.getId())
                ? friendship.getReceiver()
                : friendship.getSender();

        return UserDto.builder()
                .username(otherUser.getUsername())
                .currentRate(otherUser.getCurrentRate())
                .imgUrl(otherUser.getImgUrl())
                .status(friendStatusMapper.map(currentUser, friendship))
                .requestedAt(friendship.getRequestedAt())
                .updatedAt(friendship.getUpdatedAt())
                .build();
    }
}
