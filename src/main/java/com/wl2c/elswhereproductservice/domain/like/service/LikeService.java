package com.wl2c.elswhereproductservice.domain.like.service;

public interface LikeService {

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
     * 캐싱되어있는 경우에는 true/false로 반환하지만, 캐싱되어있지 않다면 null을 반환한다.
     *
     * @param productId 상품 ID
     * @param userId    사용자 ID
     * @return  사용자가 좋아요를 눌렀는지 반환. 캐싱된 데이터가 없다면 null 반환
     */
    boolean isLiked(Long productId, Long userId);

    /**
     * '좋아요' 개수 가져오기.
     * 메모리에 '좋아요' 개수가 없다면 캐싱한다.
     *
     * @param productId 상품 ID
     * @return 좋아요 개수
     */
    int getCountOfLikes(Long productId);

    /**
     * 특정 사용자에 대한 '좋아요' 기록 dump
     *
     * @param userId 사용자 ID
     */
    void dumpToDbForUserId(Long userId);

//    /**
//     * 가장 많은 좋아요를 받은 상품을 확인한다.
//     * 좋아요 개수가 같은 경우를 고려해서 List로 반환
//     *
//     * @return 가장 많은 좋아요를 받은 상품 정보(id, 개수) 리스트 반환
//     */
//    List<ResponseMostLikedProductDto> mostLiked();
}
