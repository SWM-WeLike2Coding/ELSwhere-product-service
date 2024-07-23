package com.wl2c.elswhereproductservice.domain.product.model.entity;

import com.wl2c.elswhereproductservice.domain.product.model.IssuerState;
import com.wl2c.elswhereproductservice.global.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Issuer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issuer_id")
    private Long id;

    @NotNull
    private String issuerName;

    @ColumnDefault("'INACTIVE'")
    @Enumerated(STRING)
    private IssuerState issuerState;

    @Builder
    private Issuer(@NonNull String issuerName,
                   IssuerState issuerState) {
        this.issuerName = issuerName;
        this.issuerState = issuerState;
    }
}
