package com.example.springboot_midterm.controller;

import com.example.springboot_midterm.model.Category;
import com.example.springboot_midterm.model.Product;
import com.example.springboot_midterm.model.Staff;
import com.example.springboot_midterm.service.CategoryService;
import com.example.springboot_midterm.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/stock")
public class StockController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public StockController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @ModelAttribute("user")
    public Staff getLoggedInUser(HttpSession session) {
        return (Staff) session.getAttribute("loggedInUser");
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(@RequestParam(value = "catId", required = false) Long catId,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            Model model, HttpSession session) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        List<Product> allProducts = productService.getAllProducts();
        List<Category> allCategories = categoryService.getAllCategories();

        List<Product> filteredProducts = allProducts;
        if (catId != null && catId > 0) {
            filteredProducts = filteredProducts.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getCatId().equals(catId))
                    .collect(Collectors.toList());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim().toLowerCase();
            filteredProducts = filteredProducts.stream()
                    .filter(p -> p.getPName().toLowerCase().contains(kw) ||
                            (p.getCategory() != null && p.getCategory().getCatName().toLowerCase().contains(kw)))
                    .collect(Collectors.toList());
        }

        model.addAttribute("products", filteredProducts);
        model.addAttribute("allProducts", allProducts);
        model.addAttribute("categories", allCategories);
        model.addAttribute("totalProducts", allProducts.size());
        model.addAttribute("totalCategories", allCategories.size());

        // Low stock items (sQty <= 5 and not yet expired)
        List<Product> lowStock = allProducts.stream()
                .filter(p -> p.getSQty() != null && p.getSQty() <= 5 && !p.isExpired())
                .collect(Collectors.toList());
        model.addAttribute("lowStockProducts", lowStock);
        model.addAttribute("lowStockCount", lowStock.size());

        // Expired products (same as Admin: shelf-life passed / expiredDate < now)
        List<Product> expired = productService.getExpiredProducts();
        model.addAttribute("expiredProducts", expired);
        model.addAttribute("expiredCount", expired.size());
        model.addAttribute("expiringSoonProducts", expired); // compatibility alias
        model.addAttribute("expiringSoonCount", expired.size()); // compatibility alias
        model.addAttribute("totalLossFromExpired", productService.getTotalLossFromExpiredProducts());
        model.addAttribute("totalExpiredUnits", productService.getTotalExpiredUnits());

        model.addAttribute("selectedCatId", catId);
        model.addAttribute("keyword", keyword);

        return "stock/dashboard";
    }

    // --- QUICK ADD STOCK ---
    @PostMapping("/products/quick-add/{id}")
    public String quickAddStock(@PathVariable("id") Long id,
                                @RequestParam(value = "amount", defaultValue = "10") int amount,
                                @RequestParam(value = "redirect", required = false) String redirect,
                                RedirectAttributes ra) {
        Product p = productService.getProductById(id);
        if (p != null) {
            int currentQty = (p.getSQty() != null) ? p.getSQty() : 0;
            p.setSQty(currentQty + amount);
            productService.saveProduct(p);
            ra.addFlashAttribute("successMessage", "Added " + amount + " units to " + p.getPName() + " (Total: " + p.getSQty() + ")!");
        }
        if ("products".equalsIgnoreCase(redirect)) {
            return "redirect:/stock/products";
        }
        return "redirect:/stock";
    }

    // --- PRODUCT CRUD ---
    @GetMapping("/products")
    public String listProducts(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "catId", required = false) Long catId,
                               @RequestParam(value = "filter", required = false) String filter,
                               Model model) {
        List<Product> allProducts = productService.getAllProducts();
        List<Category> allCategories = categoryService.getAllCategories();

        // Global counts for filter button badges (strictly expired products, same as Admin)
        long expiredCount = productService.getExpiredProducts().size();

        long lowStockCount = allProducts.stream()
                .filter(p -> p != null && p.getSQty() != null && p.getSQty() <= 5 && !p.isExpired())
                .count();

        List<Product> filtered = allProducts;

        // Apply quick button filter (expired products, sorted by earliest expiry)
        if ("expired".equalsIgnoreCase(filter)) {
            filtered = filtered.stream()
                    .filter(p -> p != null && p.isExpired())
                    .sorted((p1, p2) -> {
                        if (p1.getExpiredDate() == null) return 1;
                        if (p2.getExpiredDate() == null) return -1;
                        return p1.getExpiredDate().compareTo(p2.getExpiredDate());
                    })
                    .collect(Collectors.toList());
        } else if ("lowstock".equalsIgnoreCase(filter)) {
            filtered = filtered.stream()
                    .filter(p -> p != null && p.getSQty() != null && p.getSQty() <= 5 && !p.isExpired())
                    .sorted(java.util.Comparator.comparingInt(p -> p.getSQty() != null ? p.getSQty() : 0))
                    .collect(Collectors.toList());
        }

        // Apply category filter
        if (catId != null && catId > 0) {
            filtered = filtered.stream()
                    .filter(p -> p != null && p.getCategory() != null && p.getCategory().getCatId().equals(catId))
                    .collect(Collectors.toList());
        }

        // Apply search keyword filter
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim().toLowerCase();
            filtered = filtered.stream()
                    .filter(p -> p != null && (p.getPName().toLowerCase().contains(kw) ||
                            (p.getCategory() != null && p.getCategory().getCatName().toLowerCase().contains(kw))))
                    .collect(Collectors.toList());
        }

        model.addAttribute("products", filtered);
        model.addAttribute("categories", allCategories);
        model.addAttribute("selectedCatId", catId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeFilter", filter);
        model.addAttribute("expiredCount", expiredCount);
        model.addAttribute("expiringCount", expiredCount);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("totalProducts", allProducts.size());
        return "stock/product-list";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "stock/product-form";
    }

    @PostMapping(value = "/products/save", consumes = {"multipart/form-data", "application/x-www-form-urlencoded"})
    public String saveProduct(@ModelAttribute("product") Product product,
                               @RequestParam(value = "imageFile", required = false) org.springframework.web.multipart.MultipartFile imageFile,
                               RedirectAttributes ra) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String originalFilename = imageFile.getOriginalFilename();
                String ext = ".jpg";
                if (originalFilename != null && originalFilename.contains(".")) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String newFileName = "snack_" + System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0, 8) + ext;

                // Save to src/main/resources/static/uploads
                java.nio.file.Path uploadDir = java.nio.file.Paths.get("src/main/resources/static/uploads");
                if (!java.nio.file.Files.exists(uploadDir)) {
                    java.nio.file.Files.createDirectories(uploadDir);
                }
                java.nio.file.Path destination = uploadDir.resolve(newFileName);
                java.nio.file.Files.copy(imageFile.getInputStream(), destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // Also copy to target/classes/static/uploads if target classes folder exists
                java.nio.file.Path targetDir = java.nio.file.Paths.get("target/classes/static/uploads");
                if (java.nio.file.Files.exists(targetDir)) {
                    try (java.io.InputStream in = java.nio.file.Files.newInputStream(destination)) {
                        java.nio.file.Files.copy(in, targetDir.resolve(newFileName), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                }

                product.setImageUrl("/uploads/" + newFileName);
            } catch (Exception e) {
                // Ignore and keep existing URL if upload encounters an issue
            }
        }
        productService.saveProduct(product);
        ra.addFlashAttribute("successMessage", "Product '" + product.getPName() + "' saved successfully!");
        return "redirect:/stock/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "stock/product-form";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            Product p = productService.getProductById(id);
            String name = (p != null) ? p.getPName() : "Product #" + id;
            productService.deleteProduct(id);
            ra.addFlashAttribute("successMessage", "Product '" + name + "' deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Could not delete product: " + e.getMessage());
        }
        return "redirect:/stock/products";
    }

    // --- CATEGORY CRUD ---
    @GetMapping("/categories")
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        List<Product> products = productService.getAllProducts();

        // Calculate product count per category
        Map<Long, Long> productCounts = new HashMap<>();
        for (Category cat : categories) {
            long count = products.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getCatId().equals(cat.getCatId()))
                    .count();
            productCounts.put(cat.getCatId(), count);
        }

        model.addAttribute("categories", categories);
        model.addAttribute("productCounts", productCounts);
        model.addAttribute("category", new Category());
        return "stock/category-list";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute("category") Category category, RedirectAttributes ra) {
        categoryService.saveCategory(category);
        ra.addFlashAttribute("successMessage", "Category '" + category.getCatName() + "' saved successfully!");
        return "redirect:/stock/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategoryForm(@PathVariable("id") Long id, Model model) {
        List<Category> categories = categoryService.getAllCategories();
        List<Product> products = productService.getAllProducts();

        Map<Long, Long> productCounts = new HashMap<>();
        for (Category c : categories) {
            long count = products.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getCatId().equals(c.getCatId()))
                    .count();
            productCounts.put(c.getCatId(), count);
        }

        model.addAttribute("categories", categories);
        model.addAttribute("productCounts", productCounts);
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "stock/category-list";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            Category c = categoryService.getCategoryById(id);
            String name = (c != null) ? c.getCatName() : "Category #" + id;
            categoryService.deleteCategory(id);
            ra.addFlashAttribute("successMessage", "Category '" + name + "' deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Cannot delete category with associated products: " + e.getMessage());
        }
        return "redirect:/stock/categories";
    }
}
