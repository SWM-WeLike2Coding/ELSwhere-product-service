package com.wl2c.elswhereproductservice.global.error.exception;

import org.springframework.http.HttpStatus;

public class NotSupportedMethodException extends LocalizedMessageException {
    public NotSupportedMethodException(Throwable t) {
        super(t, HttpStatus.METHOD_NOT_ALLOWED, "notsupport.http-method");
    }
}
