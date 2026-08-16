package com.example.springboot_midterm.model;

import jakarta.persistence.*;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cat_id")
    private Long catId;

    @Column(name = "cat_name", nullable = false, unique = true)
    private String catName;

    public Category() {}

    public Category(String catName) {
        this.catName = catName;
    }

    public Category(Long catId, String catName) {
        this.catId = catId;
        this.catName = catName;
    }

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
}
