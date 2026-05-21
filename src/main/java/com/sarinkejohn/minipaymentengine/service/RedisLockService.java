package com.sarinkejohn.minipaymentengine.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RedisLockService {

    private final StringRedisTemplate redisTemplate;
    // Fallback in-memory lock registry in case Redis is unavailable (e.g., during offline unit/integration tests)
    private final ConcurrentHashMap<String, Boolean> localLocks = new ConcurrentHashMap<>();

    public RedisLockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Acquire a distributed lock.
     * @param lockKey The key to lock on
     * @param expireInSeconds The expiration time in seconds
     * @return true if lock acquired successfully, false otherwise
     */
    public boolean acquireLock(String lockKey, long expireInSeconds) {
        try {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(
                    lockKey,
                    "LOCKED",
                    Duration.ofSeconds(expireInSeconds)
            );
            return success != null && success;
        } catch (Exception e) {
            log.warn("Redis is unavailable, falling back to in-memory lock for key: {}. Error: {}", lockKey, e.getMessage());
            // In-memory fallback for tests
            return localLocks.putIfAbsent(lockKey, Boolean.TRUE) == null;
        }
    }

    /**
     * Release a lock.
     * @param lockKey The key to unlock
     */
    public void releaseLock(String lockKey) {
        try {
            redisTemplate.delete(lockKey);
        } catch (Exception e) {
            log.warn("Redis is unavailable, releasing in-memory lock for key: {}", lockKey);
            localLocks.remove(lockKey);
        }
    }
}
