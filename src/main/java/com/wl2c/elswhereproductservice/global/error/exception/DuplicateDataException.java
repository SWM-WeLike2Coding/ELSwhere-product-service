package com.wl2c.elswhereproductservice.global.error.exception;

import org.springframework.http.HttpStatus;

public class DuplicateDataException extends LocalizedMessageException{
    public DuplicateDataException(){
        super(HttpStatus.BAD_REQUEST, "duplicate.data");
    }


    public DuplicateDataException(Throwable t) {
        super(t, HttpStatus.BAD_REQUEST, "duplicate.data");
    }
}
