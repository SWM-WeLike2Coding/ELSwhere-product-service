package com.wl2c.elswhereproductservice.domain.like.repository.impl;

import com.wl2c.elswhereproductservice.domain.like.model.LikeEntry;
import com.wl2c.elswhereproductservice.domain.like.model.LikeState;
import com.wl2c.elswhereproductservice.domain.like.model.LikeTarget;
import com.wl2c.elswhereproductservice.domain.like.repository.LikeMemoryRepository;
import com.wl2c.elswhereproductservice.global.config.redis.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wl2c.elswhereproductservice.global.config.redis.RedisKeys.combine;

@Repository
@RequiredArgsConstructor
public class LikeRedisRepository implements LikeMemoryRepository {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void like(Long productId, Long userId) {
        String key = combine(RedisKeys.LIKE_KEY, LikeTarget.Product, userId);
        redisTemplate.opsForHash().put(key, productId.toString(), LikeState.LIKED.toString());

        key = combine(RedisKeys.LIKE_USERS_KEY, LikeTarget.Product);
        redisTemplate.opsForSet().add(key, userId.toString());
        setIsLiked(productId, userId, true);
    }

    @Override
    public void cancelLike(Long productId, Long userId) {
        String key = combine(RedisKeys.LIKE_KEY, LikeTarget.Product, userId);
        redisTemplate.opsForHash().put(key, productId.toString(), LikeState.CANCELLED.toString());

        key = combine(RedisKeys.LIKE_USERS_KEY, LikeTarget.Product);
        redisTemplate.opsForSet().add(key, userId.toString());
        setIsLiked(productId, userId, false);
    }

    @Override
    public Boolean isLiked(Long productId, Long userId) {
        String key = combine(RedisKeys.LIKE_PRODUCTS_KEY, LikeTarget.Product, userId);
        Object value = redisTemplate.opsForHash().get(key, productId.toString());
        if (value == null) {
            return null;
        }
        return value.equals(LikeState.LIKED.name());
    }

    @Override
    public int getCachedLikeCount(Long productId) {
        String key = combine(RedisKeys.LIKE_COUNT_KEY, LikeTarget.Product);
        Double value = redisTemplate.opsForZSet().score(key, String.valueOf(productId));
        if (value == null) {
            return -1;
        }
        return value.intValue();
    }

    @Override
    public void setIsLiked(Long productId, Long userId, boolean isLiked) {
        String key = combine(RedisKeys.LIKE_PRODUCTS_KEY, LikeTarget.Product, userId);
        String value = LikeState.CANCELLED.toString();
        if (isLiked) {
            value = LikeState.LIKED.toString();
        }
        redisTemplate.opsForHash().put(key, productId.toString(), value);
    }

    @Override
    public void setLikeCount(Long productId, int count, Duration expiresAfter) {
        String key = combine(RedisKeys.LIKE_COUNT_KEY, LikeTarget.Product);
        redisTemplate.opsForZSet().addIfAbsent(key, String.valueOf(productId), count);
        redisTemplate.expire(key, expiresAfter);
    }

    @Override
    public void increaseLikeCount(Long productId) {
        String key = combine(RedisKeys.LIKE_COUNT_KEY, LikeTarget.Product);
        redisTemplate.opsForZSet().incrementScore(key, String.valueOf(productId), 1);
    }

    @Override
    public void decreaseLikeCount(Long productId) {
        String key = combine(RedisKeys.LIKE_COUNT_KEY, LikeTarget.Product);
        redisTemplate.opsForZSet().incrementScore(key, String.valueOf(productId), -1);
    }

    @Override
    public List<LikeEntry> getAllLikesAndClear(Long userId) {
        String key = combine(RedisKeys.LIKE_KEY, LikeTarget.Product, userId);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        redisTemplate.delete(key);

        key = combine(RedisKeys.LIKE_USERS_KEY, LikeTarget.Product);
        redisTemplate.opsForSet().remove(key, userId.toString());

        return entries.entrySet().stream()
                .map(entry -> {
                    Long productId = Long.valueOf((String) entry.getKey());
                    LikeState state = LikeState.of((String) entry.getValue());
                    return new LikeEntry(productId, state);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, List<LikeEntry>> getAllLikesAndClear() {
        String key = combine(RedisKeys.LIKE_USERS_KEY, LikeTarget.Product);
        Set<String> members = redisTemplate.opsForSet().members(key);

        if (members == null) {
            return new HashMap<>();
        }

        return members.stream()
                .map(Long::valueOf)
                .collect(Collectors.toMap(
                        userId -> userId,
                        userId -> getAllLikesAndClear(userId)
                ));
    }
}
