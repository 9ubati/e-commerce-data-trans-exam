package com.datatrans.ecommerce.productms.common;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.operationHelper.errorHandler.AccessDenied;
import com.operationHelper.errorHandler.InvalidParameters; // <-- use the EXACT package your exception is in

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(InvalidParameters.class)
    public ResponseEntity<?> handleInvalidParameters(InvalidParameters ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", 400,
                "error", "InvalidParameters",
                "message", "One or more parameters are invalid.",
                "details", ex.getErrors()   // comes from your custom exception
            ));
    }
    
    @ExceptionHandler(AccessDenied.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String,Object> onAccessDenied(AccessDenied ex) {
      return Map.of("error", "forbidden", "message", ex.getMessage());
    }
}
