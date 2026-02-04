package com.clashcode.backend.Model;

import com.clashcode.backend.enums.*;
import com.clashcode.backend.model.Friend;
import com.clashcode.backend.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class FriendTest {

    @Test
    void testFriendGettersAndSetters() {
        Friend friend = new Friend();
        User sender = new User();
        sender.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        LocalDateTime now = LocalDateTime.now();

        friend.setId(1L);
        friend.setSender(sender);
        friend.setReceiver(receiver);
        friend.setStatus(FriendRequestStatus.PENDING);
        friend.setRequestedAt(now);
        friend.setUpdatedAt(now);

        assertEquals(1L, friend.getId());
        assertEquals(sender, friend.getSender());
        assertEquals(receiver, friend.getReceiver());
        assertEquals(FriendRequestStatus.PENDING, friend.getStatus());
        assertEquals(now, friend.getRequestedAt());
        assertEquals(now, friend.getUpdatedAt());
    }

    @Test
    void testFriendBuilder() {
        User sender = new User();
        sender.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        LocalDateTime now = LocalDateTime.now();

        Friend friend = Friend.builder()
                .id(1L)
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.ACCEPTED)
                .requestedAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, friend.getId());
        assertEquals(sender, friend.getSender());
        assertEquals(receiver, friend.getReceiver());
        assertEquals(FriendRequestStatus.ACCEPTED, friend.getStatus());
        assertEquals(now, friend.getRequestedAt());
        assertEquals(now, friend.getUpdatedAt());
    }

    @Test
    void testFriendNoArgsConstructor() {
        Friend friend = new Friend();
        assertNotNull(friend);
    }

    @Test
    void testFriendAllArgsConstructor() {
        User sender = new User();
        User receiver = new User();
        LocalDateTime now = LocalDateTime.now();

        Friend friend = new Friend(1L, sender, receiver, FriendRequestStatus.PENDING, now, now);

        assertEquals(1L, friend.getId());
        assertEquals(sender, friend.getSender());
        assertEquals(receiver, friend.getReceiver());
        assertEquals(FriendRequestStatus.PENDING, friend.getStatus());
        assertEquals(now, friend.getRequestedAt());
        assertEquals(now, friend.getUpdatedAt());
    }
}