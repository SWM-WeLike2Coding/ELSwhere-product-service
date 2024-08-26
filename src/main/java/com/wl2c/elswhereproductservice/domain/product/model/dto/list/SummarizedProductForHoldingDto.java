package com.wl2c.elswhereproductservice.domain.product.model.dto.list;

import com.querydsl.core.annotations.QueryProjection;
import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class SummarizedProductForHoldingDto {
    @Schema(description = "상품 id", example = "1")
    private final Long id;

    @Schema(description = "발행 회사", example = "oo투자증권")
    private final String issuer;

    @Schema(description = "상품명", example = "oo투자증권 99999")
    private final String name;

    @Schema(description = "상품 유형", example = "STEP_DOWN or LIZARD or MONTHLY_PAYMENT or ETC")
    private final ProductType productType;

    @Schema(description = "기초자산", example = "KOSPI200 Index / HSCEI Index / S&P500 Index")
    private final String equities;

    @Schema(description = "수익률", example = "20.55")
    private final BigDecimal yieldIfConditionsMet;

    @Schema(description = "다음 상환평가일", example = "2027-01-13")
    private final LocalDate nextRepaymentEvaluationDate;

    public SummarizedProductForHoldingDto(Product product,
                                          LocalDate nextRepaymentEvaluationDate) {
        this.id = product.getId();
        this.issuer = product.getIssuer();
        this.name = product.getName();
        this.productType = product.getType();
        this.equities = product.getEquities();
        this.yieldIfConditionsMet = product.getYieldIfConditionsMet();
        this.nextRepaymentEvaluationDate = nextRepaymentEvaluationDate;
    }
}
