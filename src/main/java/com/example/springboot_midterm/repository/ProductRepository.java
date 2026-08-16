package com.example.springboot_midterm.repository;

import com.example.springboot_midterm.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find products that are expired (expiredDate <= given date)
    List<Product> findByExpiredDateLessThanEqual(LocalDate date);
    
    // Find active products for users (not expired or expiredDate > today, and SQty > 0)
    @Query("SELECT p FROM Product p WHERE (p.expiredDate IS NULL OR p.expiredDate > :date) AND p.sQty > 0")
    List<Product> findAvailableProducts(LocalDate date);

    // Find products with low stock (SQty <= threshold)
    List<Product> findBySQtyLessThanEqual(Integer threshold);

    // Search products by name (case-insensitive) and available
    @Query("SELECT p FROM Product p WHERE (p.expiredDate IS NULL OR p.expiredDate > :date) AND p.sQty > 0 AND LOWER(p.pName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchAvailableProducts(String keyword, LocalDate date);

    // Filter by Category and available
    @Query("SELECT p FROM Product p WHERE (p.expiredDate IS NULL OR p.expiredDate > :date) AND p.sQty > 0 AND p.category.catId = :catId")
    List<Product> findAvailableProductsByCategory(Long catId, LocalDate date);

    // Filter by Category and keyword and available
    @Query("SELECT p FROM Product p WHERE (p.expiredDate IS NULL OR p.expiredDate > :date) AND p.sQty > 0 AND p.category.catId = :catId AND LOWER(p.pName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchAvailableProductsByCategory(Long catId, String keyword, LocalDate date);
}
