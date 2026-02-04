package com.clashcode.backend.Dto;

import com.clashcode.backend.dto.FriendDto;
import com.clashcode.backend.enums.FriendStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FriendDtoTest {

    @Test
    void testBuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();

        FriendDto friend = FriendDto.builder()
                .username("kero")
                .currentRate(1500)
                .imgUrl("https://example.com/avatar.png")
                .status(FriendStatus.FRIENDS)
                .requestedAt(now)
                .updatedAt(now)
                .build();

        assertEquals("kero", friend.getUsername());
        assertEquals(1500, friend.getCurrentRate());
        assertEquals("https://example.com/avatar.png", friend.getImgUrl());
        assertEquals(FriendStatus.FRIENDS, friend.getStatus());
        assertEquals(now, friend.getRequestedAt());
        assertEquals(now, friend.getUpdatedAt());
    }

    @Test
    void testSetters() {
        FriendDto friend = new FriendDto();
        LocalDateTime now = LocalDateTime.now();

        friend.setUsername("alice");
        friend.setCurrentRate(1450);
        friend.setImgUrl("https://example.com/alice.png");
        friend.setStatus(FriendStatus.NONE);
        friend.setRequestedAt(now);
        friend.setUpdatedAt(now);

        assertEquals("alice", friend.getUsername());
        assertEquals(1450, friend.getCurrentRate());
        assertEquals("https://example.com/alice.png", friend.getImgUrl());
        assertEquals(FriendStatus.NONE, friend.getStatus());
        assertEquals(now, friend.getRequestedAt());
        assertEquals(now, friend.getUpdatedAt());
    }
}
