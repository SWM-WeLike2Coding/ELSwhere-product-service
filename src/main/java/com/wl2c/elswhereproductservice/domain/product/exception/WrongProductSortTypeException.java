package com.wl2c.elswhereproductservice.domain.product.exception;

import com.wl2c.elswhereproductservice.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class WrongProductSortTypeException extends LocalizedMessageException {
    public WrongProductSortTypeException() {
        super(HttpStatus.BAD_REQUEST, "invalid.product-sort-type");
    }
}
