package com.example.springboot_midterm.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.springboot_midterm.exception.ResourceNotFoundException;
import com.example.springboot_midterm.model.Product;
import com.example.springboot_midterm.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    public List<Product> getExpiredProducts() {
        return productRepository.findByExpiredDateLessThanEqual(LocalDate.now());
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts(LocalDate.now());
    }

    public List<Product> searchAndFilterAvailableProducts(String keyword, Long catId) {
        LocalDate today = LocalDate.now();
        String kw = (keyword != null) ? keyword.trim() : null;
        boolean hasKeyword = kw != null && !kw.isEmpty();
        boolean hasCategory = catId != null && catId > 0;

        if (hasKeyword && hasCategory) {
            return productRepository.searchAvailableProductsByCategory(catId, kw, today);
        } else if (hasKeyword) {
            return productRepository.searchAvailableProducts(kw, today);
        } else if (hasCategory) {
            return productRepository.findAvailableProductsByCategory(catId, today);
        } else {
            return productRepository.findAvailableProducts(today);
        }
    }

    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findBySQtyLessThanEqual(threshold);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }
}
