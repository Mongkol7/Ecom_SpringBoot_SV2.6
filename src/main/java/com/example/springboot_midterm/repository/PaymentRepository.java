package com.example.springboot_midterm.repository;

import com.example.springboot_midterm.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByOrderByPaymentDateDesc();

    @Query("SELECT p.paymentMethod, COUNT(p), SUM(p.paidAmount) FROM Payment p GROUP BY p.paymentMethod")
    List<Object[]> findPaymentSummaryByMethod();
}
