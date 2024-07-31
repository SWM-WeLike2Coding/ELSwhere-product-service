package com.wl2c.elswhereproductservice.domain.product.exception;

import com.wl2c.elswhereproductservice.global.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class ProductEarlyRepaymentEvaluationDateFoundException extends LocalizedMessageException {
    public ProductEarlyRepaymentEvaluationDateFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.product-early-repayment-evaluation-date");
    }
}
