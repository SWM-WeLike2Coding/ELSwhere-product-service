package com.wl2c.elswhereproductservice.domain.product.model.dto.response;

import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class ResponseProductComparisonTargetDto {
    @Schema(description = "상품 id", example = "1")
    private final Long id;

    @Schema(description = "발행 회사", example = "oo투자증권")
    private final String issuer;

    @Schema(description = "상품명", example = "oo투자증권 99999")
    private final String name;

    @Schema(description = "조건 충족시 수익률(연, %)", example = "10.2")
    private final BigDecimal yieldIfConditionsMet;

    @Schema(description = "기초자산", example = "KOSPI200 Index / HSCEI Index / S&P500 Index")
    private final String equities;

    @Schema(description = "상품유형", example = "STEP_DOWN")
    private final ProductType type;

    @Schema(description = "상품 유형 정보(전체)", example = "스텝다운 (85-85-85-80-75-70) NoKI<br/>만기 3년,조기상환 평가주기 4개월")
    private final String productFullInfo;

    @Schema(description = "상품 유형 정보", example = "85-85-85-80-75-70")
    private final String productInfo;

    @Schema(description = "낙인 값", example = "45, 낙인 값이 없을 시 null return")
    private final Integer knockIn;

    @Schema(description = "최대손실률(%)", example = "-100")
    private final BigDecimal maximumLossRate;

    @Schema(description = "청약 시작일", example = "2024-06-14")
    private final LocalDate subscriptionStartDate;

    @Schema(description = "청약 마감일", example = "2024-06-21")
    private final LocalDate subscriptionEndDate;

    @Schema(description = "AI가 판단한 상품 안전도", example = "0.89")
    private final BigDecimal safetyScore;

    public ResponseProductComparisonTargetDto(Product product, BigDecimal safetyScore) {
        this.id = product.getId();
        this.issuer = product.getIssuer();
        this.name = product.getName();
        this.yieldIfConditionsMet = product.getYieldIfConditionsMet();
        this.equities = product.getEquities();
        this.type = product.getType();
        this.productFullInfo = product.getProductFullInfo();
        this.productInfo = product.getProductInfo();
        this.knockIn = product.getKnockIn();
        this.maximumLossRate = product.getMaximumLossRate();
        this.subscriptionStartDate = product.getSubscriptionStartDate();
        this.subscriptionEndDate = product.getSubscriptionEndDate();
        this.safetyScore = safetyScore;
    }
}
