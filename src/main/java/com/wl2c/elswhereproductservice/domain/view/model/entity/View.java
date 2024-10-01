package com.wl2c.elswhereproductservice.domain.view.model.entity;

import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class View extends BaseEntity {

    @Id
    @Column(name = "view_id", nullable = false)
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "view_id", referencedColumnName = "view_id")
    private Product product;

    private Long totalViewCount;

    private int dailyViewCount;

    @Builder
    public View(Product product,
                Long totalViewCount,
                int dailyViewCount) {
        this.product = product;
        this.totalViewCount = totalViewCount;
        this.dailyViewCount = dailyViewCount;
    }

}
