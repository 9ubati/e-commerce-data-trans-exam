package com.datatrans.ecommerce.userms.common;

import com.operationHelper.errorHandler.InvalidParameters; // <-- use the EXACT package your exception is in
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Map;

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
}
