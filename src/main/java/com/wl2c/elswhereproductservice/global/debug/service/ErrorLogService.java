package com.wl2c.elswhereproductservice.global.debug.service;

import com.wl2c.elswhereproductservice.global.debug.model.ErrorLog;
import com.wl2c.elswhereproductservice.global.model.dto.ErrorResponseDto;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ErrorLogService {

    private final Map<String, ErrorLog> errors = new HashMap<>();

    public void logError(String trackingId, Throwable t, ErrorResponseDto dto) {
        String errorLog = ExceptionUtils.getStackTrace(t);
        errors.put(trackingId, new ErrorLog(dto, errorLog));
    }

    public String findErrorLog(String trackingId) {
        ErrorLog log = errors.get(trackingId);
        if (log != null) {
            return log.toString();
        } else {
            return "No error log found for that tracking ID: " + trackingId;
        }
    }
}
