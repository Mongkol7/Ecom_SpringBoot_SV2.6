package com.example.springboot_midterm.service;

import com.example.springboot_midterm.exception.InsufficientStockException;
import com.example.springboot_midterm.exception.ResourceNotFoundException;
import com.example.springboot_midterm.model.*;
import com.example.springboot_midterm.repository.ProductRepository;
import com.example.springboot_midterm.repository.SaleDetailRepository;
import com.example.springboot_midterm.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;

    public SaleService(SaleRepository saleRepository,
                       SaleDetailRepository saleDetailRepository,
                       ProductRepository productRepository,
                       PaymentService paymentService) {
        this.saleRepository = saleRepository;
        this.saleDetailRepository = saleDetailRepository;
        this.productRepository = productRepository;
        this.paymentService = paymentService;
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAllByOrderBySaleDateDesc();
    }

    public List<Sale> getSalesByCustomer(Long customerSId) {
        return saleRepository.findByCustomerSIdOrderBySaleDateDesc(customerSId);
    }

    public Sale getSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale record not found with ID: " + id));
    }

    public List<Object[]> getTopSellingCategories() {
        return saleDetailRepository.findTopSellingCategories();
    }

    public java.util.Map<Long, Long> getProductSalesQuantityMap() {
        List<Object[]> results = saleDetailRepository.findTopSellingProducts();
        java.util.Map<Long, Long> map = new java.util.HashMap<>();
        for (Object[] row : results) {
            if (row != null && row.length >= 2 && row[0] != null) {
                Long pId = (Long) row[0];
                Number qty = (Number) row[1];
                map.put(pId, qty != null ? qty.longValue() : 0L);
            }
        }
        return map;
    }

    public Double getTotalRevenue() {
        Double total = saleRepository.sumTotalRevenue();
        return total != null ? total : 0.0;
    }

    @Transactional
    public Sale processPurchase(Staff customer, Map<Long, Integer> cartItems, String paymentMethod) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart cannot be empty for checkout.");
        }

        double totalAmount = 0.0;
        Sale sale = new Sale(customer, 0.0);

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

            if (product.isExpired()) {
                throw new InsufficientStockException("Cannot purchase expired product: " + product.getPName());
            }

            if (product.getSQty() < quantity) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getPName() +
                        ". Available: " + product.getSQty() + ", Requested: " + quantity);
            }

            // Deduct stock quantity
            product.setSQty(product.getSQty() - quantity);
            productRepository.save(product);

            double subtotal = product.getPrice() * quantity;
            totalAmount += subtotal;

            SaleDetail detail = new SaleDetail(sale, product, quantity, product.getPrice());
            sale.getDetails().add(detail);
        }

        sale.setTotalAmount(totalAmount);
        sale.setStatus("COMPLETED");

        // Save Sale (cascades details)
        Sale savedSale = saleRepository.save(sale);

        // Process Payment (strictly COMPLETED)
        String txnRef = paymentService.generateTransactionReference(paymentMethod);
        Payment payment = new Payment(savedSale, paymentMethod, totalAmount, txnRef);
        paymentService.savePayment(payment);

        savedSale.setPayment(payment);
        return savedSale;
    }
}
