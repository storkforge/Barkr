package org.storkforge.barkr.web.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.storkforge.barkr.exceptions.AccountNotFound;
import org.storkforge.barkr.exceptions.PostNotFound;
import org.storkforge.barkr.util.ErrorResponseUtil;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFound.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFound(AccountNotFound e) {
        Map<String, Object> responseMessage = ErrorResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);

    }

    @ExceptionHandler(PostNotFound.class)
    public ResponseEntity<Map<String, Object>> handlePostNotFound(PostNotFound e) {
        Map<String, Object> responseMessage = ErrorResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
    }


}
