package com.wl2c.elswhereproductservice.domain.like.repository.impl;

import com.wl2c.elswhereproductservice.domain.like.model.LikeEntry;
import com.wl2c.elswhereproductservice.domain.like.model.LikeState;
import com.wl2c.elswhereproductservice.domain.like.model.LikeTarget;
import com.wl2c.elswhereproductservice.global.config.redis.RedisKeys;
import com.wl2c.elswhereproductservice.util.base.AbstractContainerRedisTest;
import com.wl2c.elswhereproductservice.util.test.FullIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.wl2c.elswhereproductservice.global.config.redis.RedisKeys.combine;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@FullIntegrationTest
public class LikeRedisRepositoryTest extends AbstractContainerRedisTest {

    @Autowired
    private LikeRedisRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    @DisplayName("like 엔티티가 잘 추가되는가?")
    void like() {
        // given
        ProductLikeKey key = new ProductLikeKey();

        // when
        repository.like(key.productId, key.userId);

        // then
        assertThat(key.getLike(redisTemplate)).isEqualTo(LikeState.LIKED.name());
        assertThat(key.isLiked(redisTemplate)).isEqualTo(true);
    }

    @Test
    @DisplayName("중복 추가시 덮어쓰기")
    void addDuplicatedPostLike() {
        // given
        ProductLikeKey key = new ProductLikeKey();
        key.putLike(redisTemplate, "0");

        // when
        repository.like(key.productId, key.userId);

        // then
        assertThat(key.getLike(redisTemplate)).isEqualTo(LikeState.LIKED.name());
        assertThat(key.isLiked(redisTemplate)).isEqualTo(true);
    }

    @Test
    @DisplayName("좋아요 취소 처리 - Entity가 있을 때")
    void cancelLike() {
        // given
        ProductLikeKey key = new ProductLikeKey();
        repository.like(key.productId, key.userId);

        // when
        repository.cancelLike(key.productId, key.userId);

        // then
        assertThat(key.getLike(redisTemplate)).isEqualTo(LikeState.CANCELLED.name());
        assertThat(key.isLiked(redisTemplate)).isEqualTo(false);
    }

    @Test
    @DisplayName("좋아요 취소 처리 - Entity가 없을 때")
    void cancelLikeWhenNoEntry() {
        // given
        ProductLikeKey key = new ProductLikeKey();

        // when
        repository.cancelLike(key.productId, key.userId);

        // then
        assertThat(key.getLike(redisTemplate)).isEqualTo(LikeState.CANCELLED.name());
        assertThat(key.isLiked(redisTemplate)).isEqualTo(false);
    }

    @Test
    @DisplayName("좋아요 Entity가 존재하는지? - 있는 경우")
    void isPostLiked() {
        // given
        ProductLikeKey key = new ProductLikeKey();
        key.setLiked(redisTemplate, true);

        // when
        Boolean value = repository.isLiked(key.productId, key.userId);

        // then
        assertThat(value).isEqualTo(true);
    }

    @Test
    @DisplayName("좋아요 Entity가 존재하는지? - 취소된 경우")
    void isPostLikedCancelled() {
        // given
        ProductLikeKey key = new ProductLikeKey();
        key.setLiked(redisTemplate, false);

        // when
        Boolean value = repository.isLiked(key.productId, key.userId);

        // then
        assertThat(value).isEqualTo(false);
    }

    @Test
    @DisplayName("좋아요 Entity가 존재하는지? - 캐싱이 안된 경우")
    void isPostLikedNoEntity() {
        // given
        ProductLikeKey key = new ProductLikeKey();

        // when
        Boolean value = repository.isLiked(key.productId, key.userId);

        // then
        assertThat(value).isEqualTo(null);
    }

    @Test
    @DisplayName("좋아요 수 가져오기 - 캐싱된 경우")
    void getCachedLikeCount() {
        // given
        ProductLikeKey key = new ProductLikeKey();
        key.setCount(redisTemplate, key.productId, 3);

        // when
        int count = repository.getCachedLikeCount(key.productId);

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("좋아요 수 가져오기 - 캐싱안된 경우")
    void getCachedLikeCountNoCached() {
        // given
        ProductLikeKey key = new ProductLikeKey();

        // when
        int count = repository.getCachedLikeCount(key.productId);

        // then
        assertThat(count).isEqualTo(-1);
    }

    @Test
    @DisplayName("좋아요 수 직접 캐싱")
    void setLikeCount() {
        // given
        ProductLikeKey key = new ProductLikeKey();

        // when
        repository.setLikeCount(key.productId, 8, Duration.ofHours(1));

        // then
        assertThat(key.getCount(redisTemplate, key.productId)).isEqualTo(8);
    }

    @Test
    @DisplayName("좋아요 수 증가")
    void increaseLikes() {
        // given
        ProductLikeKey key = new ProductLikeKey();
        key.setCount(redisTemplate, key.productId, 10);

        // when
        repository.increaseLikeCount(key.productId);

        // then
        assertThat(key.getCount(redisTemplate, key.productId)).isEqualTo(11);
    }

    @Test
    @DisplayName("좋아요 수 감소")
    void decreaseLikes() {
        // given
        ProductLikeKey key = new ProductLikeKey();
        key.setCount(redisTemplate, key.productId, 10);

        // when
        repository.decreaseLikeCount(key.productId);

        // then
        assertThat(key.getCount(redisTemplate, key.productId)).isEqualTo(9);
    }

    @Test
    @DisplayName("특정 유저의 캐싱된 모든 좋아요 가져오고 삭제가 잘 되는지?")
    void getAllLikesAndClearForUser() {
        // given
        final int count = 10;
        for (int i = 0; i < count; i++) {
            ProductLikeKey key = new ProductLikeKey(i + 100L, 100L);
            repository.like(key.productId, key.userId);
        }

        // when
        List<LikeEntry> likes = repository.getAllLikesAndClear(100L);

        // then
        String key = combine(RedisKeys.LIKE_KEY, LikeTarget.Product, 100L);
        Long size = redisTemplate.opsForHash().size(key);
        assertThat(size).isEqualTo(0);

        key = combine(RedisKeys.LIKE_USERS_KEY, LikeTarget.Product);
        Boolean isMember = redisTemplate.opsForSet().isMember(key, "100");
        assertThat(isMember).isEqualTo(false);

        assertThat(likes.size()).isEqualTo(count);
    }

    @Test
    @DisplayName("캐싱된 모든 좋아요 가져오고 삭제가 잘 되는지?")
    void getAllPostLikesAndClear() {
        // given
        final int count = 10;
        for (int i = 0; i < count; i++) {
            ProductLikeKey key = new ProductLikeKey(i + 100L, i + 100L);
            repository.like(key.productId, key.userId);
        }

        // when
        Map<Long, List<LikeEntry>> likes = repository.getAllLikesAndClear();

        // then
        String key = combine(RedisKeys.LIKE_USERS_KEY, LikeTarget.Product);
        Long size = redisTemplate.opsForHash().size(key);
        assertThat(size).isEqualTo(0);

        for (int i = 0; i < count; i++) {
            key = combine(RedisKeys.LIKE_KEY, LikeTarget.Product, i + 100L);
            size = redisTemplate.opsForHash().size(key);
            assertThat(size).isEqualTo(0);
        }

        assertThat(likes.size()).isEqualTo(count);
    }


    private static class ProductLikeKey {
        private static final Random RAND = new Random();
        private final Long productId;
        private final Long userId;
        private final String keyString;

        public ProductLikeKey() {
            this(RAND.nextLong(), RAND.nextLong());
        }

        public ProductLikeKey(Long productId, Long userId) {
            this.productId = productId;
            this.userId = userId;
            this.keyString = combine(RedisKeys.LIKE_KEY, LikeTarget.Product, userId);
        }

        public Object getLike(StringRedisTemplate redisTemplate) {
            return redisTemplate.opsForHash().get(keyString, productId.toString());
        }

        public void putLike(StringRedisTemplate redisTemplate, String data) {
            redisTemplate.opsForHash().put(keyString, productId.toString(), data);
        }

        public int getCount(StringRedisTemplate redisTemplate, Long productId) {
            String key = combine(RedisKeys.LIKE_COUNT_KEY, LikeTarget.Product);
            return redisTemplate.opsForZSet().score(key, String.valueOf(productId)).intValue();
        }

        public void setCount(StringRedisTemplate redisTemplate, Long productId, int count) {
            String key = combine(RedisKeys.LIKE_COUNT_KEY, LikeTarget.Product);
            redisTemplate.opsForZSet().addIfAbsent(key, String.valueOf(productId), count);
        }

        public boolean isLiked(StringRedisTemplate redisTemplate) {
            String key = combine(RedisKeys.LIKE_PRODUCTS_KEY, LikeTarget.Product, userId);
            Object value = redisTemplate.opsForHash().get(key, productId.toString());
            return LikeState.LIKED.name().equals(value);
        }

        public void setLiked(StringRedisTemplate redisTemplate, boolean isLiked) {
            String key = combine(RedisKeys.LIKE_PRODUCTS_KEY, LikeTarget.Product, userId);
            String value = LikeState.CANCELLED.name();
            if (isLiked) {
                value = LikeState.LIKED.name();
            }
            redisTemplate.opsForHash().put(key, productId.toString(), value);
        }
    }

}
