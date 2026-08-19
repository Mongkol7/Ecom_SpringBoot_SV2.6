package com.example.springboot_midterm.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pid")
    private Long pId;

    @Column(name = "pname", nullable = false)
    private String pName;

    @Column(name = "sqty", nullable = false)
    private Integer sQty;

    @Column(name = "price", nullable = false)
    private Double price;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "expired_date")
    private LocalDate expiredDate;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "cat_id", nullable = false)
    private Category category;

    public Product() {}

    public Product(String pName, Integer sQty, Double price, LocalDate expiredDate, Category category) {
        this.pName = pName;
        this.sQty = sQty;
        this.price = price;
        this.expiredDate = expiredDate;
        this.category = category;
    }

    public Product(String pName, Integer sQty, Double price, LocalDate expiredDate, Category category, String imageUrl) {
        this.pName = pName;
        this.sQty = sQty;
        this.price = price;
        this.expiredDate = expiredDate;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public Long getPId() {
        return pId;
    }

    public void setPId(Long pId) {
        this.pId = pId;
    }

    public String getPName() {
        return pName;
    }

    public void setPName(String pName) {
        this.pName = pName;
    }

    public Integer getSQty() {
        return sQty;
    }

    public void setSQty(Integer sQty) {
        this.sQty = sQty;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isExpired() {
        return expiredDate != null && expiredDate.isBefore(LocalDate.now());
    }
}
