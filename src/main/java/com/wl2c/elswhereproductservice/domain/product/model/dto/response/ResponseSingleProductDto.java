package com.wl2c.elswhereproductservice.domain.product.model.dto.response;

import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Getter
public class ResponseSingleProductDto {

    @Schema(description = "상품 id", example = "1")
    private final Long id;

    @Schema(description = "발행 회사", example = "oo투자증권")
    private final String publisher;

    @Schema(description = "상품명", example = "oo투자증권 99999")
    private final String name;

    @Schema(description = "기초자산", example = "KOSPI200 Index / HSCEI Index / S&P500 Index")
    private final String equities;

    @Schema(description = "기초자산 수", example = "3")
    private final int equityCount;

    @Schema(description = "낙인 값", example = "45, 낙인 값이 없을 시 null return")
    private final Integer knockIn;

    @Schema(description = "기초자산 당 변동성", example = "KOSPI200 Index : 20.98% / HSCEI Index : 21.09% / S&P500 Index : 19.72%")
    private final String volatilites;

    @Schema(description = "자동조기상환평가일", example = "1차: 2024년 12월 20일 / 2차: 2025년 06월 20일 / 3차: 2025년 12월 19일")
    private final String earlyRepaymentEvaluationDates;

    @Schema(description = "발행일", example = "2024-06-21")
    private final LocalDate issuedDate;

    @Schema(description = "만기일", example = "2027-06-21")
    private final LocalDate maturityDate;

    @Schema(description = "조건 충족시 수익률(연, %)", example = "10.2")
    private final BigDecimal yieldIfConditionsMet;

    @Schema(description = "최대손실률(%)", example = "-100")
    private final BigDecimal maximumLossRate;

    @Schema(description = "청약 시작일", example = "2024-06-14")
    private final LocalDate subscriptionStartDate;

    @Schema(description = "청약 마감일", example = "2024-06-21")
    private final LocalDate subscriptionEndDate;

    @Schema(description = "최초기준가격평가일", example = "2024-06-21")
    private final LocalDate initialBasePriceEvaluationDate;

    @Schema(description = "상품유형", example = "STEP_DOWN")
    private final ProductType type;

    @Schema(description = "상품 유형 정보(전체)", example = "스텝다운 (85-85-85-80-75-70) NoKI<br/>만기 3년,조기상환 평가주기 4개월")
    private final String productFullInfo;

    @Schema(description = "상품 유형 정보", example = "85-85-85-80-75-70")
    private final String productInfo;

    @Schema(description = "비고", example = "2024.06.21 13:00 청약종료")
    private final String remarks;

    @Schema(description = "홈페이지 링크", example = "https://...")
    private final String link;

    @Schema(description = "간의투자설명서 링크", example = "https://...")
    private final String summaryInvestmentProspectusLink;

    @Schema(description = "각 기초자산 별 티커 정보")
    private final Map<String, String> equityTickerSymbols;

    public ResponseSingleProductDto(Product product, Map<String, String> equityTickerSymbols) {
        this.id = product.getId();
        this.publisher = product.getPublisher();
        this.name = product.getName();
        this.equities = product.getEquities();
        this.equityCount = product.getEquityCount();
        this.knockIn = product.getKnockIn();
        this.volatilites = product.getVolatilites();
        this.earlyRepaymentEvaluationDates = product.getEarlyRepaymentEvaluationDates();
        this.issuedDate = product.getIssuedDate();
        this.maturityDate = product.getMaturityDate();
        this.yieldIfConditionsMet = product.getYieldIfConditionsMet();
        this.maximumLossRate = product.getMaximumLossRate();
        this.subscriptionStartDate = product.getSubscriptionStartDate();
        this.subscriptionEndDate = product.getSubscriptionEndDate();
        this.initialBasePriceEvaluationDate = product.getInitialBasePriceEvaluationDate();
        this.type = product.getType();
        this.productFullInfo = product.getProductFullInfo();
        this.productInfo = product.getProductInfo();
        this.remarks = product.getRemarks();
        this.link = product.getLink();
        this.summaryInvestmentProspectusLink = product.getSummaryInvestmentProspectusLink();
        this.equityTickerSymbols = equityTickerSymbols;
    }
}
