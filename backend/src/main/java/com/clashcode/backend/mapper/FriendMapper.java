package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.UserDto;
import com.clashcode.backend.enums.FriendRequestStatus;
import com.clashcode.backend.enums.FriendStatus;
import com.clashcode.backend.model.Friend;
import com.clashcode.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class FriendMapper {
    public UserDto toUserDto(Friend friendship, User currentUser) {
        boolean isSender = friendship.getSender().getId().equals(currentUser.getId());
        User otherUser = isSender ? friendship.getReceiver() : friendship.getSender();

        return UserDto.builder()
                .username(otherUser.getUsername())
                .currentRate(otherUser.getCurrentRate())
                .imgUrl(otherUser.getImgUrl())
                .status(determineFriendStatus(friendship, isSender))
                .requestedAt(friendship.getRequestedAt())
                .updatedAt(friendship.getUpdatedAt())
                .build();
    }

    private FriendStatus determineFriendStatus(Friend friendship, boolean isSender) {
        if (friendship.getStatus() == FriendRequestStatus.ACCEPTED) {
            return FriendStatus.FRIENDS;
        }
        return isSender ? FriendStatus.PENDING_SENT : FriendStatus.PENDING_RECEIVED;
    }
}
