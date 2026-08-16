package com.example.springboot_midterm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @OneToOne
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // CASH, KHQR, CREDIT_CARD

    @Column(name = "paid_amount", nullable = false)
    private Double paidAmount;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus = "COMPLETED";

    @Column(name = "transaction_ref", nullable = false)
    private String transactionRef;

    public Payment() {
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = "COMPLETED";
    }

    public Payment(Sale sale, String paymentMethod, Double paidAmount, String transactionRef) {
        this.sale = sale;
        this.paymentMethod = paymentMethod;
        this.paidAmount = paidAmount;
        this.transactionRef = transactionRef;
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = "COMPLETED";
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }
}
