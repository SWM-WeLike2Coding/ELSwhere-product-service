package com.wl2c.elswhereproductservice.global.error.exception;

import com.wl2c.elswhereproductservice.global.error.FieldErrorResult;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BindingFailedException extends LocalizedMessageException {
    public BindingFailedException(BindException e) {
        super(e, HttpStatus.BAD_REQUEST, "");
    }

    @Override
    public List<Object> getMessages(MessageSource messageSource, Locale locale) {
        return ((BindException) getCause()).getFieldErrors().stream()
                .map((err) -> new FieldErrorResult(err.getField(), messageSource.getMessage(err, locale)))
                .collect(Collectors.toList());
    }
}
