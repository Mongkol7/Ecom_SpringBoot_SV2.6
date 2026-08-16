package com.example.springboot_midterm.repository;

import com.example.springboot_midterm.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    List<Sale> findAllByOrderBySaleDateDesc();
    
    List<Sale> findByCustomerSIdOrderBySaleDateDesc(Long customerSId);

    @Query("SELECT SUM(s.totalAmount) FROM Sale s WHERE s.status = 'COMPLETED'")
    Double sumTotalRevenue();

    List<Sale> findBySaleDateBetweenOrderBySaleDateDesc(LocalDateTime start, LocalDateTime end);
}
