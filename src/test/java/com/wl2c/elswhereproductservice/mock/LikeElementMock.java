package com.wl2c.elswhereproductservice.mock;

import com.wl2c.elswhereproductservice.domain.like.model.entity.LikeElement;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;

public class LikeElementMock {

    public static LikeElement create(Product product, int count) {
        return LikeElement.builder()
                .product(product)
                .count(count)
                .build();
    }
}
