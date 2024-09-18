package com.wl2c.elswhereproductservice.domain.like.repository;

import com.wl2c.elswhereproductservice.domain.like.model.LikeEntry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface LikeMemoryRepository {

    /**
     * '좋아요' 추가
     *
     * @param productId 상품 ID
     * @param userId    사용자 ID
     */
    void like(Long productId, Long userId);

    /**
     * '좋아요' 취소 처리. 실제로 삭제되진않고 취소되었다고 마킹만 해둔다.
     * 그래야 나중에 캐싱되었는지 확인할 수 있기 때문이다.
     *
     * @param productId 상품 ID
     * @param userId    사용자 ID
     */
    void cancelLike(Long productId, Long userId);

    /**
     * 메모리에서 사용자가 '좋아요'를 눌렀는지 확인한다.
     * 캐싱되어있는 경우에는 true/false로 반환하지만, 캐싱되어있지 않다면 null을
     * 반환한다.
     *
     * @param productId 요소 ID
     * @param userId    사용자 ID
     * @return 사용자가 좋아요를 눌렀는지 반환. 캐싱된 데이터가 없다면 null반환.
     */
    Boolean isLiked(Long productId, Long userId);

    /**
     * 좋아요 여부를 메모리에 캐싱한다.
     *
     * @param productId 상품 ID
     * @param userId    사용자 ID
     * @param isLiked   좋아요 여부
     */
    void setIsLiked(Long productId, Long userId, boolean isLiked);

    /**
     * 메모리에 캐싱된 좋아요 개수 확인.
     *
     * @param productId 요소 ID
     * @return 캐싱된 좋아요 개수. 없으면 -1리턴.
     */
    int getCachedLikeCount(Long productId);

    /**
     * 좋아요 개수 캐싱
     *
     * @param productId 요소 ID
     * @param count     좋아요 개수
     */
    void setLikeCount(Long productId, int count, Duration expiresAfter);

    /**
     * 좋아요 개수 1 증가
     *
     * @param productId 요소 ID
     */
    void increaseLikeCount(Long productId);

    /**
     * 좋아요 개수 1 감소
     *
     * @param productId 요소 ID
     */
    void decreaseLikeCount(Long productId);

    /**
     * 캐싱된 모든 '좋아요' 데이터중에서 특정 유저의 것들만 가져오고, 모두 삭제한다.
     *
     * @param userId 사용자 ID
     * @return '좋아요' entities
     */
    List<LikeEntry> getAllLikesAndClear(Long userId);

    /**
     * 메모리에 저장된 모든 '좋아요' 데이터를 가져오고, 모두 삭제한다.
     *
     * @return '좋아요' entities
     */
    Map<Long, List<LikeEntry>> getAllLikesAndClear();
}
