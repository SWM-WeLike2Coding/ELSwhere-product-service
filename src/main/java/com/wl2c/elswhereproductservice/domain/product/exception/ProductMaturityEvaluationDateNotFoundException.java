package com.wl2c.elswhereproductservice.domain.product.exception;

import com.wl2c.elswhereproductservice.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class ProductMaturityEvaluationDateNotFoundException extends LocalizedMessageException {
    public ProductMaturityEvaluationDateNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.product-maturity-repayment-evaluation-date");
    }

    public ProductMaturityEvaluationDateNotFoundException(Long productId) {
        super(HttpStatus.NOT_FOUND, "notfound.product-maturity-repayment-evaluation-date", "productId: " + productId);
    }
}
