package com.example.springboot_midterm.repository;

import com.example.springboot_midterm.model.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleDetailRepository extends JpaRepository<SaleDetail, Long> {

    @Query("SELECT c.catName, SUM(sd.quantity) AS totalQty, SUM(sd.subtotal) AS totalRevenue " +
           "FROM SaleDetail sd JOIN sd.product p JOIN p.category c " +
           "GROUP BY c.catName ORDER BY SUM(sd.quantity) DESC")
    List<Object[]> findTopSellingCategories();

    @Query("SELECT sd.product.pId, SUM(sd.quantity) AS totalQty " +
           "FROM SaleDetail sd " +
           "GROUP BY sd.product.pId ORDER BY SUM(sd.quantity) DESC")
    List<Object[]> findTopSellingProducts();
}
