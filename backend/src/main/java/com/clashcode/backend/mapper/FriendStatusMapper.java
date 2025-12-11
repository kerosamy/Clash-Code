package com.clashcode.backend.mapper;

import com.clashcode.backend.enums.FriendRequestStatus;
import com.clashcode.backend.enums.FriendStatus;
import com.clashcode.backend.model.Friend;
import com.clashcode.backend.model.User;

public class FriendStatusMapper {

    private FriendStatusMapper() {}

    public FriendStatus map(User requester, Friend friendship) {
        if (friendship == null) return FriendStatus.NONE;
        FriendRequestStatus status = friendship.getStatus();

        if (status == FriendRequestStatus.ACCEPTED)
            return FriendStatus.FRIENDS;
        else if(requester.equals(friendship.getSender()))
            return FriendStatus.PENDING_SENT;
        else if(requester.equals(friendship.getReceiver()))
            return FriendStatus.PENDING_RECEIVED;
        else
            return FriendStatus.NONE;
    }
}
