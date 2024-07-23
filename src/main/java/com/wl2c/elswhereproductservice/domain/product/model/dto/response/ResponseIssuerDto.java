package com.wl2c.elswhereproductservice.domain.product.model.dto.response;

import com.wl2c.elswhereproductservice.domain.product.model.entity.Issuer;
import com.wl2c.elswhereproductservice.domain.product.model.entity.TickerSymbol;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ResponseIssuerDto {

    @Schema(description = "발행사 id", example = "1")
    private final Long id;

    @Schema(description = "발행사 명", example = "NH투자증권")
    private final String issuer;

    public ResponseIssuerDto(Issuer issuer) {
        this.id = issuer.getId();
        this.issuer = issuer.getIssuerName();
    }
}
