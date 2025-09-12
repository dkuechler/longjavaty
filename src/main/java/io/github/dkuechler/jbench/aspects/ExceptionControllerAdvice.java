package io.github.dkuechler.jbench.aspects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.dkuechler.jbench.exceptions.FailedPaymentException;
import io.github.dkuechler.jbench.model.ErrorDetails;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(FailedPaymentException.class)
    public ResponseEntity<ErrorDetails> handleFailedPaymentException() {
        ErrorDetails errorDetails = new ErrorDetails("Payment failed due to invalid amount.");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }
    
}
