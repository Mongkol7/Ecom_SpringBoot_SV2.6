package com.example.springboot_midterm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sale")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "sale_date", nullable = false)
    private LocalDateTime saleDate;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "status", nullable = false)
    private String status = "COMPLETED";

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Staff customer;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleDetail> details = new ArrayList<>();

    @OneToOne(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    public Sale() {
        this.saleDate = LocalDateTime.now();
    }

    public Sale(Staff customer, Double totalAmount) {
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.saleDate = LocalDateTime.now();
        this.status = "COMPLETED";
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Staff getCustomer() {
        return customer;
    }

    public void setCustomer(Staff customer) {
        this.customer = customer;
    }

    public List<SaleDetail> getDetails() {
        return details;
    }

    public void setDetails(List<SaleDetail> details) {
        this.details = details;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
