package com.wl2c.elswhereproductservice.domain.product.exception;

import com.wl2c.elswhereproductservice.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class TodayReceivedProductsNotFoundException extends LocalizedMessageException {
    public TodayReceivedProductsNotFoundException() { super(HttpStatus.NOT_FOUND, "notfound.today-received-products"); }
}
