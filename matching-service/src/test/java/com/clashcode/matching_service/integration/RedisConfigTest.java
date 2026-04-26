package com.clashcode.matching_service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisConfigTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void testRedisTemplateBean() {
        assertNotNull(redisTemplate);
    }

    @Test
    void testRedisTemplateConfiguration() {
        assertNotNull(redisTemplate.getConnectionFactory());
    }
}

