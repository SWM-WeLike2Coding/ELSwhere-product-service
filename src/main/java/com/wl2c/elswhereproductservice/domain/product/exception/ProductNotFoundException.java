package com.wl2c.elswhereproductservice.domain.product.exception;

import com.wl2c.elswhereproductservice.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends LocalizedMessageException {
    public ProductNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.product");
    }
}
