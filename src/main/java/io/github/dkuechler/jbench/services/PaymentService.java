package io.github.dkuechler.jbench.services;

import org.springframework.stereotype.Service;

import io.github.dkuechler.jbench.exceptions.FailedPaymentException;
import io.github.dkuechler.jbench.model.PaymentDetails;

@Service
public class PaymentService {
    public PaymentDetails processPayment(PaymentDetails paymentDetails) {
        if (paymentDetails.amount() <= 0) {
            throw new FailedPaymentException();
        }
        return paymentDetails;
    }
    
}
