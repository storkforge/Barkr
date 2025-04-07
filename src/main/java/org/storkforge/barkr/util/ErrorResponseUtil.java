package org.storkforge.barkr.util;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ErrorResponseUtil {
    private ErrorResponseUtil() {}

    public static Map<String, Object> createErrorResponse(HttpStatus status, RuntimeException e){

        if (status == null || e == null) {
            throw new IllegalArgumentException("Parameters 'status' and 'e' cannot be null");
        }


        return Map.of(
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "status", status.value(),
                "error",  status.getReasonPhrase(),
                "message", e.getMessage()
        );


    }
}
