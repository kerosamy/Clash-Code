package com.clashcode.backend.mapper;

import com.clashcode.backend.enums.FriendRequestStatus;
import com.clashcode.backend.enums.FriendStatus;
import com.clashcode.backend.model.Friend;
import com.clashcode.backend.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FriendStatusMapperTest {
    private final FriendStatusMapper mapper = new FriendStatusMapper();

    @Test
    void test_map_shouldReturnNone_whenFriendshipIsNull() {
        assertEquals(FriendStatus.NONE, mapper.map(User.builder().id(1L).build(), null));
    }

    @Test
    void test_map_shouldReturnFriends_whenStatusIsAccepted() {
        User user = User.builder().id(1L).build();
        Friend f = Friend.builder().status(FriendRequestStatus.ACCEPTED)
                .sender(user).receiver(User.builder().id(2L).build()).build();
        assertEquals(FriendStatus.FRIENDS, mapper.map(user, f));
    }

    @Test
    void test_map_shouldReturnPendingSent_whenUserIsSender() {
        User user = User.builder().id(1L).build();
        Friend f = Friend.builder().status(FriendRequestStatus.PENDING)
                .sender(user).receiver(User.builder().id(2L).build()).build();
        assertEquals(FriendStatus.PENDING_SENT, mapper.map(user, f));
    }

    @Test
    void test_map_shouldReturnPendingReceived_whenUserIsReceiver() {
        User user = User.builder().id(1L).build();
        Friend f = Friend.builder().status(FriendRequestStatus.PENDING)
                .sender(User.builder().id(2L).build()).receiver(user).build();
        assertEquals(FriendStatus.PENDING_RECEIVED, mapper.map(user, f));
    }

    @Test
    void test_map_shouldReturnNone_whenUserIsNeitherSenderNorReceiver() {
        User user = User.builder().id(3L).build();
        Friend f = Friend.builder().status(FriendRequestStatus.PENDING)
                .sender(User.builder().id(1L).build())
                .receiver(User.builder().id(2L).build()).build();
        assertEquals(FriendStatus.NONE, mapper.map(user, f));
    }
}