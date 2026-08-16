package com.example.springboot_midterm.config;

import com.example.springboot_midterm.model.*;
import com.example.springboot_midterm.repository.*;
import com.example.springboot_midterm.service.SaleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StaffRepository staffRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final SaleService saleService;

    public DataInitializer(StaffRepository staffRepository,
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           SaleService saleService) {
        this.staffRepository = staffRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.saleService = saleService;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Initialize Staff Accounts if empty
        if (staffRepository.count() == 0) {
            staffRepository.save(new Staff("admin", "123", "ADMIN", "System Administrator"));
            staffRepository.save(new Staff("stock", "123", "STOCK", "Stock Manager"));
            staffRepository.save(new Staff("user", "123", "USER", "Customer User"));
        }

        Staff customer = staffRepository.findByUserName("user").orElse(null);

        // 2. Ensure American Snack Categories Exist
        Category catChips = getOrCreateCategory("Chips & Crisps");
        Category catChocolates = getOrCreateCategory("Chocolates & Candies");
        Category catCookies = getOrCreateCategory("Cookies & Pastries");
        Category catCrackers = getOrCreateCategory("Crackers & Pretzels");
        Category catJerky = getOrCreateCategory("Jerky & Savory Snacks");

        // 3. Define 16 Iconic American Snack Products
        List<ProductSeedData> americanSnackCatalog = List.of(
            // Chips & Crisps
            new ProductSeedData("Doritos Nacho Cheese Tortilla Chips (9.25 oz)", 45, 4.99, LocalDate.now().plusMonths(12), catChips, "/images/doritos_nacho_cheese.jpg"),
            new ProductSeedData("Cheetos Crunchy Flamin' Hot Cheese Snacks (8.5 oz)", 50, 4.79, LocalDate.now().plusMonths(10), catChips, "/images/cheetos_flamin_hot.jpg"),
            new ProductSeedData("Lay's Classic Potato Chips Party Size (13 oz)", 40, 5.29, LocalDate.now().plusMonths(8), catChips, "/images/lays_classic.jpg"),
            new ProductSeedData("Pringles Sour Cream & Onion Potato Crisps (5.5 oz)", 60, 2.89, LocalDate.now().plusMonths(14), catChips, "/images/pringles_sour_cream.jpg"),

            // Chocolates & Candies
            new ProductSeedData("Reese's Peanut Butter Cups King Size (4 ct)", 75, 2.49, LocalDate.now().plusMonths(12), catChocolates, "/images/reeses_peanut_butter.jpg"),
            new ProductSeedData("Hershey's Milk Chocolate Bar Giant Size (7 oz)", 55, 3.49, LocalDate.now().plusMonths(18), catChocolates, "/images/hersheys_chocolate.jpg"),
            new ProductSeedData("M&M's Peanut Chocolate Candies Sharing Size (10.7 oz)", 65, 4.29, LocalDate.now().plusMonths(16), catChocolates, "/images/mms_peanut.jpg"),
            new ProductSeedData("Sour Patch Kids Original Soft & Chewy Candy (8 oz)", 70, 3.19, LocalDate.now().plusMonths(12), catChocolates, "https://images.unsplash.com/photo-1582058091505-f87a2e55a40f?w=600&auto=format&fit=crop&q=80"),

            // Cookies & Pastries
            new ProductSeedData("Oreo Double Stuf Chocolate Sandwich Cookies (14 oz)", 48, 4.99, LocalDate.now().plusMonths(10), catCookies, "/images/oreo_double_stuf.jpg"),
            new ProductSeedData("Pop-Tarts Frosted Strawberry Toaster Pastries (8 ct)", 35, 3.99, LocalDate.now().plusMonths(12), catCookies, "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=600&auto=format&fit=crop&q=80"),
            new ProductSeedData("Hostess Twinkies Golden Sponge Cakes (10 ct)", 30, 4.49, LocalDate.now().plusMonths(6), catCookies, "https://images.unsplash.com/photo-1587314168485-3236d6710814?w=600&auto=format&fit=crop&q=80"),
            new ProductSeedData("Limited Batch Marshmallow Rice Treats (Expired)", 6, 1.99, LocalDate.now().minusDays(15), catCookies, "https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=600&auto=format&fit=crop&q=80"),

            // Crackers & Pretzels
            new ProductSeedData("Goldfish Baked Cheddar Snack Crackers (6.6 oz)", 55, 2.99, LocalDate.now().plusMonths(9), catCrackers, "/images/goldfish_cheddar.jpg"),
            new ProductSeedData("Cheez-It Original Baked Cheese Crackers (12.4 oz)", 42, 4.69, LocalDate.now().plusMonths(10), catCrackers, "/images/cheez_it_original.jpg"),
            new ProductSeedData("Snyder's Honey Mustard & Onion Pretzel Pieces (8 oz)", 38, 3.89, LocalDate.now().plusMonths(11), catCrackers, "https://images.unsplash.com/photo-1584947926880-99435b62b716?w=600&auto=format&fit=crop&q=80"),

            // Jerky & Savory Snacks
            new ProductSeedData("Jack Link's Original Beef Jerky Protein Snack (3.25 oz)", 32, 6.99, LocalDate.now().plusMonths(14), catJerky, "/images/jack_links_beef_jerky.jpg")
        );

        // 4. Map legacy products by index/name to replace with American snack products
        List<Product> existingProducts = productRepository.findAll();
        for (int i = 0; i < americanSnackCatalog.size(); i++) {
            ProductSeedData data = americanSnackCatalog.get(i);
            if (i < existingProducts.size()) {
                Product existing = existingProducts.get(i);
                existing.setPName(data.name);
                existing.setPrice(data.price);
                existing.setSQty(data.qty);
                existing.setExpiredDate(data.expiredDate);
                existing.setCategory(data.category);
                existing.setImageUrl(data.imageUrl);
                productRepository.save(existing);
            } else {
                productRepository.save(new Product(data.name, data.qty, data.price, data.expiredDate, data.category, data.imageUrl));
            }
        }

        // 5. Clean up any excess products beyond 16 if unreferenced
        if (existingProducts.size() > americanSnackCatalog.size()) {
            for (int i = americanSnackCatalog.size(); i < existingProducts.size(); i++) {
                try {
                    productRepository.delete(existingProducts.get(i));
                } catch (Exception ignored) {}
            }
        }

        // 6. Clean up obsolete empty categories
        List<String> legacyCategoryNames = List.of(
            "Audio & Acoustics", "Keyboards & Mice", "Displays & Monitors", "Storage & Memory", "Accessories & Gadgets",
            "Fiction & Literature", "Business & Finance", "Science & Nature", "Technology & Programming",
            "Software Engineering", "Cloud & Infrastructure", "AI & Data Science", "Cybersecurity & Networks", "Hardware & Systems"
        );
        for (String oldCatName : legacyCategoryNames) {
            categoryRepository.findAll().stream()
                    .filter(c -> c.getCatName().equalsIgnoreCase(oldCatName))
                    .findFirst()
                    .ifPresent(cat -> {
                        long count = productRepository.findAll().stream().filter(p -> p.getCategory() != null && p.getCategory().getCatId().equals(cat.getCatId())).count();
                        if (count == 0) {
                            try { categoryRepository.delete(cat); } catch (Exception ignored) {}
                        }
                    });
        }

        // 7. Seed initial sale demo if no sales exist
        if (customer != null && productRepository.count() >= 2 && saleService.getAllSales().isEmpty()) {
            List<Product> allProducts = productRepository.findAll();
            Product p1 = allProducts.get(0);
            Product p2 = allProducts.get(1);
            if (p1 != null && p2 != null && p1.getSQty() >= 2 && p2.getSQty() >= 1) {
                Map<Long, Integer> cart = new HashMap<>();
                cart.put(p1.getPId(), 2);
                cart.put(p2.getPId(), 3);
                saleService.processPurchase(customer, cart, "KHQR");
            }
        }
    }

    private Category getOrCreateCategory(String name) {
        return categoryRepository.findAll().stream()
                .filter(c -> c.getCatName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> categoryRepository.save(new Category(name)));
    }

    private static class ProductSeedData {
        final String name;
        final Integer qty;
        final Double price;
        final LocalDate expiredDate;
        final Category category;
        final String imageUrl;

        ProductSeedData(String name, Integer qty, Double price, LocalDate expiredDate, Category category, String imageUrl) {
            this.name = name;
            this.qty = qty;
            this.price = price;
            this.expiredDate = expiredDate;
            this.category = category;
            this.imageUrl = imageUrl;
        }
    }
}
