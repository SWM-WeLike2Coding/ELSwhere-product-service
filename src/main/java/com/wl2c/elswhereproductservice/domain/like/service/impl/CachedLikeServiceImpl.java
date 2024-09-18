package com.wl2c.elswhereproductservice.domain.like.service.impl;

import com.wl2c.elswhereproductservice.client.user.api.UserServiceClient;
import com.wl2c.elswhereproductservice.domain.like.model.LikeEntry;
import com.wl2c.elswhereproductservice.domain.like.model.LikeState;
import com.wl2c.elswhereproductservice.domain.like.model.entity.LikeElement;
import com.wl2c.elswhereproductservice.domain.like.repository.LikeMemoryRepository;
import com.wl2c.elswhereproductservice.domain.like.repository.LikePersistenceRepository;
import com.wl2c.elswhereproductservice.domain.like.service.LikeService;
import com.wl2c.elswhereproductservice.domain.product.exception.ProductNotFoundException;
import com.wl2c.elswhereproductservice.domain.product.model.dto.ProductLikeMessage;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.domain.product.repository.ProductRepository;
import com.wl2c.elswhereproductservice.domain.product.service.ProductLikeMessageSender;
import com.wl2c.elswhereproductservice.global.error.exception.UnexpectedException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CachedLikeServiceImpl implements LikeService {

    private final CircuitBreakerFactory circuitBreakerFactory;

    private final LikeMemoryRepository likeMemoryRepository;
    private final LikePersistenceRepository likePersistenceRepository;
    private final ProductRepository productRepository;

    private final UserServiceClient userServiceClient;
    private final ProductLikeMessageSender productLikeMessageSender;

    @Value("${app.product.like.count-cache-time}")
    private Duration countCacheTime;

    @Override
    @Transactional(readOnly = true)
    public void like(Long productId, Long userId) {
        if (!isLiked(productId, userId)) {
            likeMemoryRepository.like(productId, userId);
            likeMemoryRepository.increaseLikeCount(productId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void cancelLike(Long productId, Long userId) {
        if (isLiked(productId, userId)) {
            likeMemoryRepository.cancelLike(productId, userId);
            likeMemoryRepository.decreaseLikeCount(productId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(Long productId, Long userId) {
        Boolean liked = likeMemoryRepository.isLiked(productId, userId);
        if (liked == null) {
            CircuitBreaker circuitBreaker = circuitBreakerFactory.create("checkIsLikedCircuitBreaker");
            liked = circuitBreaker.run(
                    () -> {
                        // 사용자 DB에 해당 상품에 대한 사용자의 좋아요 기록이 되어있는지 확인
                        userServiceClient.checkIsLiked(String.valueOf(userId), productId);
                        return Boolean.TRUE;
                    },
                    throwable -> {
                        if (throwable instanceof FeignException feignException) {
                            if (feignException.status() == 404) {
                                return Boolean.FALSE;
                            }
                        }
                        throw new UnexpectedException(throwable);
                    }
            );

            likeMemoryRepository.setIsLiked(productId, userId, liked);
        }
        return liked;
    }

    @Override
    @Transactional(readOnly = true)
    public int getCountOfLikes(Long productId) {
        int count = likeMemoryRepository.getCachedLikeCount(productId);
        if (count == -1) {
//            count = likePersistenceRepository.countById(productId);
            count = likePersistenceRepository.findLikeCountByProductId(productId);
            likeMemoryRepository.setLikeCount(productId, count, countCacheTime);
        }
        return count;
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<ResponseMostLikedProductDto> mostLiked() {
//        int count = likeMemoryRepository.getCachedMostLikedCount();
//        if (count == -1) {
//            List<Like> likeList = likePersistenceRepository.findMostLiked();
//        }
//        return likeList.stream()
//                .map(like -> new ResponseMostLikedProductDto(like, count))
//                .collect(Collectors.toList());
//    }

    @Transactional
    public long dumpToDB() {
        Map<Long, Integer> productLikeChanges = new HashMap<>();

        Map<Long, List<LikeEntry>> allLikes = likeMemoryRepository.getAllLikesAndClear();
        for (Map.Entry<Long, List<LikeEntry>> ent : allLikes.entrySet()) {
            for (LikeEntry likeEntry : ent.getValue()) {
                Product product = productRepository.getReferenceById(likeEntry.getProductId());

                ProductLikeMessage productLikeMessage;
                if (likeEntry.getState() == LikeState.LIKED) {
                    // -> kafka
                    productLikeMessage = ProductLikeMessage.builder()
                            .userId(ent.getKey())
                            .productId(likeEntry.getProductId())
                            .likeState(LikeState.LIKED)
                            .build();
                    productLikeMessageSender.send("product-like", productLikeMessage);

                    // product id에 대한 좋아요 누적 +1
                    productLikeChanges.merge(product.getId(), 1, Integer::sum);

                } else if (likeEntry.getState() == LikeState.CANCELLED) {
                    // -> kafka
                    productLikeMessage = ProductLikeMessage.builder()
                            .userId(ent.getKey())
                            .productId(likeEntry.getProductId())
                            .likeState(LikeState.CANCELLED)
                            .build();
                    productLikeMessageSender.send("product-like", productLikeMessage);

                    // product id에 대한 좋아요 누적 -1
                    productLikeChanges.merge(product.getId(), -1, Integer::sum);

                }
            }
        }

        // 누적된 좋아요 개수 반영
        for (Map.Entry<Long, Integer> entry : productLikeChanges.entrySet()) {
            Long productId = entry.getKey();
            Integer delta = entry.getValue();

            Optional<LikeElement> existingProduct = likePersistenceRepository.findById(productId);
            if (existingProduct.isPresent()) {
                likePersistenceRepository.updateProductLikeCount(productId, delta);
            } else {
                Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);

                LikeElement likeElement;
                if (delta > 0) {
                    likeElement = LikeElement.builder()
                            .product(product)
                            .count(delta)
                            .build();
                } else {
                    likeElement = LikeElement.builder()
                            .product(product)
                            .build();
                }
                likePersistenceRepository.save(likeElement);
            }
        }
        productLikeChanges.clear();

        return allLikes.size();
    }

    @Override
    @Transactional
    public void dumpToDbForUserId(Long userId) {
        Map<Long, Integer> productLikeChanges = new HashMap<>();

        List<LikeEntry> allLikes = likeMemoryRepository.getAllLikesAndClear(userId);
        for (LikeEntry likeEntry : allLikes) {
            Product product = productRepository.getReferenceById(likeEntry.getProductId());

            ProductLikeMessage productLikeMessage;
            if (likeEntry.getState() == LikeState.LIKED) {
                // -> kafka
                productLikeMessage = ProductLikeMessage.builder()
                        .userId(userId)
                        .productId(likeEntry.getProductId())
                        .likeState(LikeState.LIKED)
                        .build();
                productLikeMessageSender.send("product-like", productLikeMessage);

                // product id에 대한 좋아요 누적 +1
                productLikeChanges.merge(product.getId(), 1, Integer::sum);

            } else if (likeEntry.getState() == LikeState.CANCELLED) {
                // -> kafka
                productLikeMessage = ProductLikeMessage.builder()
                        .userId(userId)
                        .productId(likeEntry.getProductId())
                        .likeState(LikeState.CANCELLED)
                        .build();
                productLikeMessageSender.send("product-like", productLikeMessage);

                // product id에 대한 좋아요 누적 -1
                productLikeChanges.merge(product.getId(), -1, Integer::sum);

            }
        }

        // 누적된 좋아요 개수 반영
        for (Map.Entry<Long, Integer> entry : productLikeChanges.entrySet()) {
            Long productId = entry.getKey();
            Integer delta = entry.getValue();

            Optional<LikeElement> existingProduct = likePersistenceRepository.findById(productId);
            if (existingProduct.isPresent()) {
                if (existingProduct.get().getCount() + delta < 0) {
                    likePersistenceRepository.updateProductLikeCount(productId, 0);
                } else {
                    likePersistenceRepository.updateProductLikeCount(productId, delta);
                }
            } else {
                Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);

                LikeElement likeElement;
                if (delta > 0) {
                    likeElement = LikeElement.builder()
                            .product(product)
                            .count(delta)
                            .build();
                } else {
                    likeElement = LikeElement.builder()
                            .product(product)
                            .build();
                }
                likePersistenceRepository.save(likeElement);
            }
        }
        productLikeChanges.clear();
    }
}
