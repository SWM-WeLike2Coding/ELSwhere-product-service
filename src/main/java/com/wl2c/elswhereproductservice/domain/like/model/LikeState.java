package com.wl2c.elswhereproductservice.domain.like.model;

public enum LikeState {
    CANCELLED,
    LIKED;

    /**
     * 이름으로 LikeState를 찾습니다.
     *
     * @param name 이름
     * @return 매칭되는 LikeState.
     */
    public static LikeState of(String name) {
        for (LikeState ent : values()) {
            if (ent.name().equals(name)) {
                return ent;
            }
        }
        return null;
    }
}
