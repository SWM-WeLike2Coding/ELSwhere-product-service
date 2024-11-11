package com.wl2c.elswhereproductservice.mock;

import com.wl2c.elswhereproductservice.domain.product.model.MaturityEvaluationDateType;
import com.wl2c.elswhereproductservice.domain.product.model.ProductState;
import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.UnderlyingAssetType;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.util.EntityUtil;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductMock {

    public static Product create(Long productId) {
        Product product = create(
                "AA증권",
                "1호",
                "삼성전자 / S&P500 / KOSPI200",
                3,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusYears(3),
                LocalDate.now().plusYears(3).minusDays(5),
                MaturityEvaluationDateType.SINGLE,
                new BigDecimal("10.423"),
                LocalDate.now().minusDays(14),
                LocalDate.now().minusDays(1),
                "95-90-85-80-75-50",
                45,
                ProductType.STEP_DOWN,
                UnderlyingAssetType.MIX,
                ProductState.ACTIVE);

        EntityUtil.injectId(Product.class, product, productId);

        return product;
    }

    public static Product create() {
        return create(
                "AA증권",
                "1호",
                "삼성전자 / S&P500 / KOSPI200",
                3,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusYears(3),
                LocalDate.now().plusYears(3).minusDays(5),
                MaturityEvaluationDateType.SINGLE,
                new BigDecimal("10.423"),
                LocalDate.now().minusDays(14),
                LocalDate.now().minusDays(1),
                "95-90-85-80-75-50",
                45,
                ProductType.STEP_DOWN,
                UnderlyingAssetType.MIX,
                ProductState.ACTIVE);
    }

    public static Product create(String issuer,
                                 String name,
                                 String equities,
                                 int equityCount,
                                 LocalDate issuedDate,
                                 LocalDate maturityDate,
                                 LocalDate maturityEvaluationDate,
                                 MaturityEvaluationDateType maturityEvaluationDateType,
                                 BigDecimal yieldIfConditionsMet,
                                 LocalDate subscriptionStartDate,
                                 LocalDate subscriptionEndDate,
                                 String productInfo,
                                 Integer knockIn,
                                 ProductType productType,
                                 UnderlyingAssetType underlyingAssetType,
                                 ProductState productState) {
        return Product.builder()
                .issuer(issuer)
                .name(name)
                .equities(equities)
                .equityCount(equityCount)
                .issuedDate(issuedDate)
                .maturityDate(maturityDate)
                .maturityEvaluationDate(maturityEvaluationDate)
                .maturityEvaluationDateType(maturityEvaluationDateType)
                .yieldIfConditionsMet(yieldIfConditionsMet)
                .subscriptionStartDate(subscriptionStartDate)
                .subscriptionEndDate(subscriptionEndDate)
                .productInfo(productInfo)
                .knockIn(knockIn)
                .productType(productType)
                .underlyingAssetType(underlyingAssetType)
                .productState(productState)
                .maximumLossRate(BigDecimal.valueOf(100))
                .productFullInfo("")
                .link("")
                .remarks("")
                .summaryInvestmentProspectusLink("")
                .earlyRepaymentEvaluationDates("")
                .volatilites("")
                .initialBasePriceEvaluationDate(null)
                .build();
    }
}
