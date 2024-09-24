package com.wl2c.elswhereproductservice.domain.like.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class LikeEntry {
    private final Long productId;
    private final LikeState state;
}
