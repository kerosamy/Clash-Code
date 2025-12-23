package com.clashcode.matching_service.service;

import com.clashcode.matching_service.dto.UserMatchingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.*;

import java.util.Set;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private RedisService redisService;

    private static final String KEY_FOR_Z_SET = "matching:queue";

    @BeforeEach
    void setup() {
        lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    void testInsertUser_ShouldAddToZSetAndHash() {
        Long userId = 1L;
        Integer rate = 1500;

        redisService.insertUser(userId, rate);

        verify(zSetOperations).add(KEY_FOR_Z_SET, "user:1", rate);
        verify(hashOperations).put(eq("matching:user:1"), eq("rate"), anyString());
        verify(hashOperations).put(eq("matching:user:1"), eq("joinedAt"), anyString());
    }

    @Test
    void testRemoveUser_ShouldRemoveFromZSetAndDeleteHash() {
        Long userId = 1L;

        redisService.removeUser(userId);

        verify(zSetOperations).remove(KEY_FOR_Z_SET, "user:1");
        verify(redisTemplate).delete("matching:user:1");
    }

    @Test
    void testGetAllUsers_ShouldReturnUsersFromZSet() {
        Set<String> mockUsers = new HashSet<>();
        mockUsers.add("user:1");
        mockUsers.add("user:2");

        when(zSetOperations.range(KEY_FOR_Z_SET, 0, -1)).thenReturn(mockUsers);

        Set<String> result = redisService.getAllUsers();

        assertEquals(2, result.size());
        assertTrue(result.contains("user:1"));
        assertTrue(result.contains("user:2"));
    }

    @Test
    void testIsUserExist_ShouldReturnTrueIfScoreExists() {
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:1")).thenReturn(1500.0);

        Boolean exists = redisService.isUserExist("user:1");

        assertTrue(exists);
    }

    @Test
    void testIsUserExist_ShouldReturnFalseIfScoreNull() {
        when(zSetOperations.score(KEY_FOR_Z_SET, "user:1")).thenReturn(null);

        Boolean exists = redisService.isUserExist("user:1");

        assertFalse(exists);
    }

    @Test
    void testGetUsersInRage_ShouldReturnCorrectSet() {
        Set<String> mockUsers = new HashSet<>();
        mockUsers.add("user:1");
        mockUsers.add("user:2");

        when(zSetOperations.rangeByScore(KEY_FOR_Z_SET, 1000, 1600)).thenReturn(mockUsers);

        Set<String> result = redisService.getUsersInRage(1000, 1600);

        assertEquals(2, result.size());
        assertTrue(result.contains("user:1"));
        assertTrue(result.contains("user:2"));
    }

    @Test
    void testGetUserData_ShouldReturnUserMatchingDto() {
        Long userId = 1L;

        when(hashOperations.get("matching:user:1", "rate")).thenReturn("1500");
        when(hashOperations.get("matching:user:1", "joinedAt")).thenReturn("1000");

        UserMatchingDto dto = redisService.getUserData(userId);

        assertNotNull(dto);
        assertEquals(userId, dto.getUserId());
        assertEquals(1500, dto.getUserRating());
        assertEquals(1000L, dto.getStartTime());
    }

    @Test
    void testGetUserData_ShouldReturnNullIfNoData() {
        Long userId = 1L;

        when(hashOperations.get("matching:user:1", "rate")).thenReturn(null);

        UserMatchingDto dto = redisService.getUserData(userId);

        assertNull(dto);
    }

    @Test
    void testGetZSetMember_ShouldReturnCorrectString() {
        assertEquals("user:42", redisService.getZSetMember(42L));
    }

    @Test
    void testGetHashKey_ShouldReturnCorrectString() {
        assertEquals("matching:user:42", redisService.getHashKey(42L));
    }

    @Test
    void testGetUserIdFromString_ShouldReturnCorrectId() {
        assertEquals(42L, redisService.getUserIdFromString("user:42"));
    }
}
