package com.wl2c.elswhereproductservice.domain.like.model.entity;

import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.global.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeElement extends BaseEntity {

    @Id
    @Column(name = "like_id", nullable = false)
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "like_id", referencedColumnName = "like_id")
    private Product product;

    @NotNull
    private int count = 0;

    @Builder
    public LikeElement(Product product,
                       int count) {
        this.product = product;
        this.count = count;
    }
    
}