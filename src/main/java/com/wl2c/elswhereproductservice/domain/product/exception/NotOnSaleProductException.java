package com.wl2c.elswhereproductservice.domain.product.exception;

import com.wl2c.elswhereproductservice.global.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class NotOnSaleProductException extends LocalizedMessageException {
    public NotOnSaleProductException() {
        super(HttpStatus.FORBIDDEN, "required.product-on-sale");
    }
}
