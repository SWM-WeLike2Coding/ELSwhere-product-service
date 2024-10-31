package com.wl2c.elswhereproductservice.domain.product.model.dto.list;

import com.querydsl.core.annotations.QueryProjection;
import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.wl2c.elswhereproductservice.domain.product.model.entity.QProduct.product;

@Getter
public class SummarizedProductDto {
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

    @Schema(description = "낙인 값", example = "45, 낙인 값이 없을 시 null return")
    private final Integer knockIn;

    @Schema(description = "청약 시작일", example = "2024-06-14")
    private final LocalDate subscriptionStartDate;

    @Schema(description = "청약 마감일", example = "2024-06-21")
    private final LocalDate subscriptionEndDate;

    @Schema(description = "AI가 판단한 스텝다운 상품 안전도", example = "0.89")
    private final BigDecimal safetyScore;

    public SummarizedProductDto(Product product, BigDecimal safetyScore) {
        this.id = product.getId();
        this.issuer = product.getIssuer();
        this.name = product.getName();
        this.productType = product.getType();
        this.equities = product.getEquities();
        this.yieldIfConditionsMet = product.getYieldIfConditionsMet();
        this.knockIn = product.getKnockIn();
        this.subscriptionStartDate = product.getSubscriptionStartDate();
        this.subscriptionEndDate = product.getSubscriptionEndDate();
        this.safetyScore = safetyScore;
    }
}
