package com.wl2c.elswhereproductservice.domain.product.exception;

import com.wl2c.elswhereproductservice.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class OnSaleProductNotFoundException extends LocalizedMessageException {
    public OnSaleProductNotFoundException() {
        super(HttpStatus.NOT_FOUND, "notfound.on-sale-product");
    }
}
