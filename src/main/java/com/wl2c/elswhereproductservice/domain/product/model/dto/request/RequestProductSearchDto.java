package com.wl2c.elswhereproductservice.domain.product.model.dto.request;

import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.UnderlyingAssetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class RequestProductSearchDto {

    @Schema(description = "기초자산 명", example = "['S&P500', 'Tesla']")
    private final List<String> equityNames;

    @Schema(description = "기초자산 수", example = "3")
    private final Integer equityCount;

    @Schema(description = "발행 회사", example = "oo투자증권")
    private final String publisher;

    @Schema(description = "최대 KI(낙인배리어)", example = "45, 낙인 값이 없을 시 null")
    private final Integer maxKnockIn;

    @Schema(description = "최소 수익률(연, %)", example = "10.2")
    private final BigDecimal minYieldIfConditionsMet;

    @Schema(description = "1차 상환 배리어", example = "90")
    private final Integer initialRedemptionBarrier;

    @Schema(description = "만기 상환 배리어", example = "65")
    private final Integer maturityRedemptionBarrier;

    @Schema(description = "상품 가입 기간", example = "3")
    private final Integer subscriptionPeriod;

    @Schema(description = "상환일 간격", example = "6")
    private final Integer redemptionInterval;

    @Schema(description = "기초자산 유형", example = "INDEX")
    private final UnderlyingAssetType equityType;

    @Schema(description = "상품 유형", example = "STEP_DOWN")
    private final ProductType type;

    @Schema(description = "청약 시작일", example = "2024-06-14")
    private final LocalDate subscriptionStartDate;

    @Schema(description = "청약 마감일", example = "2024-06-21")
    private final LocalDate subscriptionEndDate;
}
