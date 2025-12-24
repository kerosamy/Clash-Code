package com.clashcode.backend.mapper;

import com.clashcode.backend.enums.FriendRequestStatus;
import com.clashcode.backend.enums.FriendStatus;
import com.clashcode.backend.model.Friend;
import com.clashcode.backend.model.User;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FriendStatusMapper {

    public FriendStatusMapper() {}

    public FriendStatus map(User requester, Friend friendship) {
        if (friendship == null) return FriendStatus.NONE;
        FriendRequestStatus status = friendship.getStatus();

        if (status == FriendRequestStatus.ACCEPTED)
            return FriendStatus.FRIENDS;
        else if(requester.getId().equals(friendship.getSender().getId()))
            return FriendStatus.PENDING_SENT;
        else if(requester.getId().equals(friendship.getReceiver().getId()))
            return FriendStatus.PENDING_RECEIVED;
        else
            return FriendStatus.NONE;
    }
}
