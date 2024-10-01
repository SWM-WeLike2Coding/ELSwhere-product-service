package com.wl2c.elswhereproductservice.domain.view.repository.impl;

import com.wl2c.elswhereproductservice.domain.like.model.LikeTarget;
import com.wl2c.elswhereproductservice.domain.view.model.ViewState;
import com.wl2c.elswhereproductservice.domain.view.model.ViewTarget;
import com.wl2c.elswhereproductservice.domain.view.repository.ViewMemoryRepository;
import com.wl2c.elswhereproductservice.global.config.redis.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

import static com.wl2c.elswhereproductservice.global.config.redis.RedisKeys.combine;

@Repository
@RequiredArgsConstructor
public class ViewRedisRepository implements ViewMemoryRepository {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void view(Long productId, Long userId) {
        String key = combine(RedisKeys.VIEW_KEY, ViewTarget.Product, userId, LocalDate.now());
        redisTemplate.opsForHash().put(key, productId.toString(), ViewState.VIEWED.toString());

        key = combine(RedisKeys.VIEW_USERS_KEY, LikeTarget.Product, LocalDate.now());
        redisTemplate.opsForSet().add(key, userId.toString());
        setIsViewed(productId, userId);
    }

    @Override
    public Boolean isViewed(Long productId, Long userId) {
        String key = combine(RedisKeys.VIEW_PRODUCTS_KEY, ViewTarget.Product, userId, LocalDate.now());
        Object value = redisTemplate.opsForHash().get(key, productId.toString());
        if (value == null) {
            return null;
        }
        return value.equals(ViewState.VIEWED.name());
    }

    @Override
    public void setIsViewed(Long productId, Long userId) {
        String key = combine(RedisKeys.VIEW_PRODUCTS_KEY, ViewTarget.Product, userId, LocalDate.now());
        String value = ViewState.VIEWED.toString();

        redisTemplate.opsForHash().put(key, productId.toString(), value);
    }

    @Override
    public int getCachedViewCount(Long productId) {
        String key = combine(RedisKeys.VIEW_COUNT_KEY, ViewTarget.Product, LocalDate.now());
        Double value = redisTemplate.opsForZSet().score(key, String.valueOf(productId));
        if (value == null) {
            return -1;
        }
        return value.intValue();
    }

    @Override
    public void setViewCount(Long productId, Duration expiresAfter) {
        String key = combine(RedisKeys.VIEW_COUNT_KEY, ViewTarget.Product, LocalDate.now());
        redisTemplate.opsForZSet().addIfAbsent(key, String.valueOf(productId), 0);
        redisTemplate.expire(key, expiresAfter);
    }

    @Override
    public void increaseViewCount(Long productId) {
        String key = combine(RedisKeys.VIEW_COUNT_KEY, ViewTarget.Product, LocalDate.now());
        redisTemplate.opsForZSet().incrementScore(key, String.valueOf(productId), 1);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> getCachedViewCountDesc() {
        String key = combine(RedisKeys.VIEW_COUNT_KEY, ViewTarget.Product, LocalDate.now());
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
    }

    @Override
    public void deleteAllDailyViews(LocalDate localDate, Long userId) {
        String key = combine(RedisKeys.VIEW_KEY, LikeTarget.Product, userId, LocalDate.now());
        redisTemplate.delete(key);

        key = combine(RedisKeys.VIEW_USERS_KEY, LikeTarget.Product, LocalDate.now());
        redisTemplate.opsForSet().remove(key, userId.toString());

        key = combine(RedisKeys.VIEW_PRODUCTS_KEY, ViewTarget.Product, userId, LocalDate.now());
        redisTemplate.delete(key);
    }

    @Override
    public void deleteAllDailyViews(LocalDate localDate) {
        String key = combine(RedisKeys.VIEW_COUNT_KEY, ViewTarget.Product, localDate);
        redisTemplate.delete(key);

        key = combine(RedisKeys.VIEW_USERS_KEY, LikeTarget.Product, LocalDate.now());
        Set<String> members = redisTemplate.opsForSet().members(key);

        if (members != null) {
            members.stream()
                    .map(Long::parseLong)
                    .forEach(memberId -> deleteAllDailyViews(localDate, memberId));
        }
    }

}
