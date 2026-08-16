package com.example.springboot_midterm.service;

import com.example.springboot_midterm.model.Payment;
import com.example.springboot_midterm.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAllByOrderByPaymentDateDesc();
    }

    public List<Object[]> getPaymentSummaryByMethod() {
        return paymentRepository.findPaymentSummaryByMethod();
    }

    public String generateTransactionReference(String method) {
        String prefix = method != null ? method.substring(0, Math.min(3, method.length())).toUpperCase() : "TXN";
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + uuid;
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
