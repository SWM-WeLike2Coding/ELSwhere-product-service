package com.wl2c.elswhereproductservice.domain.like.service;

import com.wl2c.elswhereproductservice.domain.like.repository.LikeMemoryRepository;
import com.wl2c.elswhereproductservice.domain.like.service.impl.CachedLikeServiceImpl;
import com.wl2c.elswhereproductservice.util.base.AbstractContainerRedisTest;
import com.wl2c.elswhereproductservice.util.test.FullIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@FullIntegrationTest
@ExtendWith(MockitoExtension.class)
class CachedLikeServiceConcurrencyTest extends AbstractContainerRedisTest {

    private static final int THREAD_COUNT = 100;

    @MockBean
    private CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    private CachedLikeServiceImpl service;

    @Autowired
    private LikeMemoryRepository memoryRepository;


    @Test
    @DisplayName("동시에 k건 이상의 like를 추가하면, k건만큼 like가 추가되어야 한다.")
    void likeNoCached() throws InterruptedException {
        // given
        Long productId = 1L;
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        CircuitBreaker circuitBreakerMock = Mockito.mock(CircuitBreaker.class);
        when(circuitBreakerFactory.create(any(String.class))).thenReturn(circuitBreakerMock);
        when(circuitBreakerMock.run(any(), any())).thenReturn(Boolean.FALSE);

        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            final Long userId = 1L + i;
            new Thread(() -> {
                service.like(productId, userId);
                latch.countDown();
            }).start();
        }
        latch.await();

        // then
        assertThat(service.getCountOfLikes(productId)).isEqualTo(THREAD_COUNT);
        assertThat(memoryRepository.getAllLikesAndClear().size()).isEqualTo(THREAD_COUNT);
    }
}