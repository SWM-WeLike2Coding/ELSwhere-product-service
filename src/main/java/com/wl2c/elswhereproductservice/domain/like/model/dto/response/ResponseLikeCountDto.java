package com.wl2c.elswhereproductservice.domain.like.model.dto.response;

import lombok.Getter;

@Getter
public class ResponseLikeCountDto {

    private final int likeCount;

    public ResponseLikeCountDto(int likeCount) {
        this.likeCount = likeCount;
    }
}
