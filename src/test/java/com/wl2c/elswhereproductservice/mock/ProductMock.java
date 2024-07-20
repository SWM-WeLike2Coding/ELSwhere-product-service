package com.wl2c.elswhereproductservice.mock;

import com.wl2c.elswhereproductservice.domain.product.model.ProductState;
import com.wl2c.elswhereproductservice.domain.product.model.ProductType;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductMock {

    public static Product create(String publisher,
                                 String name,
                                 String equities,
                                 int equityCount,
                                 LocalDate issuedDate,
                                 LocalDate maturityDate,
                                 BigDecimal yieldIfConditionsMet,
                                 LocalDate subscriptionStartDate,
                                 LocalDate subscriptionEndDate,
                                 String productInfo,
                                 Integer knockIn,
                                 ProductType productType,
                                 ProductState productState) {
        return Product.builder()
                .publisher(publisher)
                .name(name)
                .equities(equities)
                .equityCount(equityCount)
                .issuedDate(issuedDate)
                .maturityDate(maturityDate)
                .yieldIfConditionsMet(yieldIfConditionsMet)
                .subscriptionStartDate(subscriptionStartDate)
                .subscriptionEndDate(subscriptionEndDate)
                .productInfo(productInfo)
                .knockIn(knockIn)
                .productType(productType)
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
