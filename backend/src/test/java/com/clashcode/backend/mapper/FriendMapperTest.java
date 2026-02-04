package com.clashcode.backend.mapper;

import com.clashcode.backend.dto.FriendDto;
import com.clashcode.backend.enums.FriendStatus;
import com.clashcode.backend.enums.UserStatus;
import com.clashcode.backend.model.Friend;
import com.clashcode.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class FriendMapperTest {

    private FriendMapper friendMapper;
    private FriendStatusMapper friendStatusMapper;

    @BeforeEach
    void setUp() {
        friendStatusMapper = Mockito.mock(FriendStatusMapper.class);
        friendMapper = new FriendMapper(friendStatusMapper);
    }

    @Test
    void test_toFriendDto_whenUserIsSender() {
        User alice = User.builder().id(1L).username("alice").build();
        User bob = User.builder().id(2L).username("bob").currentRate(1500).imgUrl("img").build();
        LocalDateTime now = LocalDateTime.now();

        Friend friendship = Friend.builder()
                .sender(alice)
                .receiver(bob)
                .requestedAt(now)
                .build();

        when(friendStatusMapper.map(alice, friendship)).thenReturn(FriendStatus.PENDING_SENT);

        FriendDto result = friendMapper.toFriendDto(friendship, alice, UserStatus.ONLINE);

        assertEquals("bob", result.getUsername());
        assertEquals(1500, result.getCurrentRate());
        assertEquals(FriendStatus.PENDING_SENT, result.getStatus());
        assertEquals(now, result.getRequestedAt());
        assertEquals(UserStatus.ONLINE, result.getUserStatus());
        assertEquals("img", result.getImgUrl());
    }

    @Test
    void test_toFriendDto_whenUserIsReceiver() {
        User alice = User.builder().id(1L).username("alice").build();
        User bob = User.builder().id(2L).username("bob").build();

        Friend friendship = Friend.builder()
                .sender(bob)
                .receiver(alice)
                .build();

        when(friendStatusMapper.map(alice, friendship)).thenReturn(FriendStatus.PENDING_RECEIVED);

        FriendDto result = friendMapper.toFriendDto(friendship, alice, UserStatus.ONLINE);

        assertEquals("bob", result.getUsername());
        assertEquals(FriendStatus.PENDING_RECEIVED, result.getStatus());
    }

    @Test
    void test_toFriendDto_withNullValues() {
        User alice = User.builder().id(1L).username("alice").build();
        User bob = User.builder().id(2L).username("bob").currentRate(null).imgUrl(null).build();

        Friend friendship = Friend.builder()
                .sender(alice)
                .receiver(bob)
                .requestedAt(null)
                .updatedAt(null)
                .build();

        when(friendStatusMapper.map(alice, friendship)).thenReturn(FriendStatus.FRIENDS);

        FriendDto result = friendMapper.toFriendDto(friendship, alice, UserStatus.OFFLINE);

        assertEquals("bob", result.getUsername());
        assertNull(result.getCurrentRate());
        assertNull(result.getImgUrl());
        assertNull(result.getRequestedAt());
        assertNull(result.getUpdatedAt());
        assertEquals(UserStatus.OFFLINE, result.getUserStatus());
    }

    @Test
    void test_toFriendDto_withOfflineStatus() {
        User user1 = User.builder().id(1L).username("user1").build();
        User user2 = User.builder().id(2L).username("user2").currentRate(2000).imgUrl("avatar.png").build();

        Friend friendship = Friend.builder()
                .sender(user1)
                .receiver(user2)
                .requestedAt(LocalDateTime.now())
                .build();

        when(friendStatusMapper.map(user1, friendship)).thenReturn(FriendStatus.FRIENDS);

        FriendDto result = friendMapper.toFriendDto(friendship, user1, UserStatus.OFFLINE);

        assertEquals(UserStatus.OFFLINE, result.getUserStatus());
    }
}