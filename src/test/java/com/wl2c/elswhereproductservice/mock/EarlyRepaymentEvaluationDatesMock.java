package com.wl2c.elswhereproductservice.mock;

import com.wl2c.elswhereproductservice.domain.product.model.entity.EarlyRepaymentEvaluationDates;
import com.wl2c.elswhereproductservice.domain.product.model.entity.Product;
import com.wl2c.elswhereproductservice.domain.product.model.entity.ProductTickerSymbol;
import com.wl2c.elswhereproductservice.domain.product.model.entity.TickerSymbol;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EarlyRepaymentEvaluationDatesMock {
    public static EarlyRepaymentEvaluationDates create(Product product,
                                                       LocalDate earlyRepaymentEvaluationDate) {
        return EarlyRepaymentEvaluationDates.builder()
                .product(product)
                .earlyRepaymentEvaluationDate(earlyRepaymentEvaluationDate)
                .build();
    }

    public static List<EarlyRepaymentEvaluationDates> createList(Product product,
                                                                 List<LocalDate> earlyRepaymentEvaluationDates) {
        List<EarlyRepaymentEvaluationDates> earlyRepaymentEvaluationDatesList = new ArrayList<>();

        for (LocalDate earlyRepaymentEvaluationDate : earlyRepaymentEvaluationDates) {
            earlyRepaymentEvaluationDatesList.add(create(product, earlyRepaymentEvaluationDate));
        }

        return earlyRepaymentEvaluationDatesList;
    }
}
