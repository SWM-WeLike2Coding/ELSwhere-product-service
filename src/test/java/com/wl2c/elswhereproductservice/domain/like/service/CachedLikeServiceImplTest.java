package com.wl2c.elswhereproductservice.domain.like.service;

import com.wl2c.elswhereproductservice.domain.like.model.LikeEntry;
import com.wl2c.elswhereproductservice.domain.like.model.LikeState;
import com.wl2c.elswhereproductservice.domain.like.model.entity.LikeElement;
import com.wl2c.elswhereproductservice.domain.like.repository.LikeMemoryRepository;
import com.wl2c.elswhereproductservice.domain.like.repository.LikePersistenceRepository;
import com.wl2c.elswhereproductservice.domain.like.service.impl.CachedLikeServiceImpl;
import com.wl2c.elswhereproductservice.domain.product.model.dto.ProductLikeMessage;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductRepository;
import com.wl2c.elswhereproductservice.domain.product.service.ProductLikeMessageSender;
import com.wl2c.elswhereproductservice.mock.ProductMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachedLikeServiceImplTest {

    @Mock
    private CircuitBreakerFactory circuitBreakerFactory;

    @Mock
    private LikeMemoryRepository memoryRepository;

    @Mock
    private LikePersistenceRepository persistenceRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductLikeMessageSender productLikeMessageSender;

    @InjectMocks
    private CachedLikeServiceImpl service;

    private final Duration cacheTime = Duration.ofHours(1);


    @Test
    @DisplayName("좋아요")
    void likeNoCached() {
        // when
        service.like(10L, 10L);

        // then
        verify(memoryRepository).like(10L, 10L);
        verify(memoryRepository).increaseLikeCount(10L);
        verify(memoryRepository).increaseLikeCountDelta(10L);
    }

    @Test
    @DisplayName("좋아요 - 이미 좋아요한 경우 무시")
    void likeAlready() {
        // given
        when(memoryRepository.isLiked(10L, 10L)).thenReturn(true);

        // when
        service.like(10L, 10L);

        // then
        verify(memoryRepository, never()).like(10L, 10L);
        verify(memoryRepository, never()).setLikeCount(any(), eq(5), eq(cacheTime));
        verify(memoryRepository, never()).setLikeCountDelta(any(), eq(cacheTime));
    }

    @Test
    @DisplayName("좋아요 취소")
    void cancelLikeNoCached() {
        // given
        when(memoryRepository.isLiked(10L, 10L)).thenReturn(true);

        // when
        service.cancelLike(10L, 10L);

        // then
        verify(memoryRepository).cancelLike(10L, 10L);
        verify(memoryRepository).decreaseLikeCount(10L);
        verify(memoryRepository).decreaseLikeCountDelta(10L);
    }

    @Test
    @DisplayName("좋아요 - 좋아요를 안한경우 무시")
    void cancelLikeAlready() {
        // given
        when(memoryRepository.isLiked(10L, 10L)).thenReturn(false);

        // when
        service.cancelLike(10L, 10L);

        // then
        verify(memoryRepository, never()).cancelLike(10L, 10L);
        verify(memoryRepository, never()).setLikeCount(any(), eq(5), eq(cacheTime));
        verify(memoryRepository, never()).setLikeCountDelta(any(), eq(cacheTime));
    }

    @Test
    @DisplayName("좋아요 확인 - 캐시에 좋아요가 등록된 경우")
    void isPostLikedCached() {
        // given
        when(memoryRepository.isLiked(10L, 10L)).thenReturn(true);

        // when
        boolean liked = service.isLiked(10L, 10L);

        // then
        assertThat(liked).isEqualTo(true);
    }

    @Test
    @DisplayName("좋아요 확인 - 캐시에 좋아요가 등록안되었고 DB에도 없는 경우")
    void isPostLikedNoCachedNoDB() {
        // given
        when(memoryRepository.isLiked(10L, 10L)).thenReturn(null);

        CircuitBreaker circuitBreakerMock = Mockito.mock(CircuitBreaker.class);
        when(circuitBreakerFactory.create(any(String.class))).thenReturn(circuitBreakerMock);
        when(circuitBreakerMock.run(any(), any())).thenReturn(false);

        // when
        boolean liked = service.isLiked(10L, 10L);

        // then
        assertThat(liked).isEqualTo(false);
        verify(memoryRepository).setIsLiked(10L, 10L, false);
    }

    @Test
    @DisplayName("좋아요 확인 - 캐시에 좋아요가 등록안되었고 DB에는 있는 경우")
    void isPostLikedNoCached() {
        // given
        when(memoryRepository.isLiked(5L, 10L)).thenReturn(null);

        CircuitBreaker circuitBreakerMock = Mockito.mock(CircuitBreaker.class);
        when(circuitBreakerFactory.create(any(String.class))).thenReturn(circuitBreakerMock);
        when(circuitBreakerMock.run(any(), any())).thenReturn(true);

        // when
        boolean liked = service.isLiked(5L, 10L);

        // then
        assertThat(liked).isEqualTo(true);
        verify(memoryRepository).setIsLiked(5L, 10L,true);
    }

    @Test
    @DisplayName("좋아요 개수 확인 - 캐싱된 경우")
    void getCountOfLikesCached() {
        // given
        when(memoryRepository.getCachedLikeCount(10L)).thenReturn(10);

        // when
        int likes = service.getCountOfLikes(10L);

        // then
        assertThat(likes).isEqualTo(10);
    }

    @Test
    @DisplayName("좋아요 개수 확인 - 캐싱안된 경우")
    void getCountOfLikesNoCached() {
        // given
        when(memoryRepository.getCachedLikeCount(10L)).thenReturn(-1);
        when(persistenceRepository.findLikeCountByProductId(10L)).thenReturn(10);

        // when
        int likes = service.getCountOfLikes(10L);

        // then
        assertThat(likes).isEqualTo(10);
    }

    @Test
    @DisplayName("Memory에 캐시된 좋아요를 DB로 dump")
    void dumpToDB() {
        // given
        Long userId = 6L;
        List<LikeEntry> likeEntries = makeLikeEntryList();

        when(memoryRepository.getAllLikesAndClear()).thenReturn(Map.of(userId, likeEntries));

        for (LikeEntry entry : likeEntries) {
            Product mockProduct = ProductMock.create(entry.getProductId());
            when(productRepository.getReferenceById(entry.getProductId())).thenReturn(mockProduct);
            when(productRepository.findById(entry.getProductId())).thenReturn(Optional.of(mockProduct));
        }

        // when
        long result = service.dumpToDB();

        // then
        assertEquals(1L, result);

        ArgumentCaptor<ProductLikeMessage> messageCaptor = ArgumentCaptor.forClass(ProductLikeMessage.class);
        verify(productLikeMessageSender, times(likeEntries.size())).send(eq("product-like"), messageCaptor.capture());

        // LikeEntry 리스트의 인덱스와 capturedMessages를 비교하여 검증
        List<ProductLikeMessage> capturedMessages = messageCaptor.getAllValues();
        for (int i = 0; i < capturedMessages.size(); i++) {
            ProductLikeMessage message = capturedMessages.get(i);
            LikeEntry entry = likeEntries.get(i);

            // userId, productId, likeState 값들이 올바르게 전달되었는지 검증
            assertEquals(entry.getProductId(), message.getProductId());
            assertEquals(userId, message.getUserId());
            assertEquals(entry.getState(), message.getLikeState());
        }

        verify(persistenceRepository, times(20)).save(any(LikeElement.class));

    }

    private static List<LikeEntry> makeLikeEntryList() {
        Stream<LikeEntry> likeStream = LongStream.range(0, 10)
                .mapToObj(i -> new LikeEntry(i, LikeState.LIKED));
        Stream<LikeEntry> cancelledStream = LongStream.range(10, 20)
                .mapToObj(i -> new LikeEntry(i, LikeState.CANCELLED));
        return Stream.concat(likeStream, cancelledStream)
                .collect(Collectors.toList());
    }
}