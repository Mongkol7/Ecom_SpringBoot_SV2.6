package com.example.springboot_midterm.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.springboot_midterm.model.Payment;
import com.example.springboot_midterm.model.Product;
import com.example.springboot_midterm.model.Sale;
import com.example.springboot_midterm.model.Staff;
import com.example.springboot_midterm.service.PaymentService;
import com.example.springboot_midterm.service.ProductService;
import com.example.springboot_midterm.service.SaleService;
import com.example.springboot_midterm.service.StaffService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StaffService staffService;
    private final ProductService productService;
    private final SaleService saleService;
    private final PaymentService paymentService;

    public AdminController(StaffService staffService,
            ProductService productService,
            SaleService saleService,
            PaymentService paymentService) {
        this.staffService = staffService;
        this.productService = productService;
        this.saleService = saleService;
        this.paymentService = paymentService;
    }

    @ModelAttribute("user")
    public Staff getLoggedInUser(HttpSession session) {
        return (Staff) session.getAttribute("loggedInUser");
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model, HttpSession session) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);
        model.addAttribute("staffCount", staffService.getAllStaff().size());

        List<Product> expired = productService.getExpiredProducts();
        model.addAttribute("expiredCount", expired.size());
        model.addAttribute("expiredProducts", expired);

        List<Product> allProducts = productService.getAllProducts();
        long lowStockCount = allProducts.stream()
                .filter(p -> p.getSQty() != null && p.getSQty() <= 3 && p.getSQty() > 0 && !p.isExpired())
                .count();
        model.addAttribute("lowStockCount", lowStockCount);

        double totalRevenue = saleService.getTotalRevenue();
        double totalLossFromExpired = productService.getTotalLossFromExpiredProducts();
        int totalExpiredUnits = productService.getTotalExpiredUnits();
        double activeInventoryValue = productService.getTotalActiveInventoryValue();
        double netRevenue = Math.max(0.0, totalRevenue - totalLossFromExpired);

        List<Sale> sales = saleService.getAllSales();
        int totalOrdersCount = sales.size();
        double averageOrderValue = (totalOrdersCount > 0) ? (totalRevenue / totalOrdersCount) : 0.0;

        double totalPotentialInventory = activeInventoryValue + totalLossFromExpired;
        double wastageRate = (totalPotentialInventory > 0) ? (totalLossFromExpired / totalPotentialInventory) * 100.0 : 0.0;

        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalLossFromExpired", totalLossFromExpired);
        model.addAttribute("totalExpiredUnits", totalExpiredUnits);
        model.addAttribute("activeInventoryValue", activeInventoryValue);
        model.addAttribute("netRevenue", netRevenue);
        model.addAttribute("totalOrdersCount", totalOrdersCount);
        model.addAttribute("averageOrderValue", averageOrderValue);
        model.addAttribute("wastageRate", wastageRate);

        model.addAttribute("recentSales", sales);
        model.addAttribute("sales", sales);
        model.addAttribute("salesList", sales);

        List<Object[]> topCategories = saleService.getTopSellingCategories();
        model.addAttribute("topCategories", topCategories);
        String topCatName = (topCategories != null && !topCategories.isEmpty()) ? String.valueOf(topCategories.get(0)[0]) : "None";
        model.addAttribute("topCategoryName", topCatName);

        return "admin/dashboard";
    }

    // CRUD Staff
    @GetMapping("/staff")
    public String listStaff(Model model) {
        List<Staff> staffList = staffService.getAllStaff();
        model.addAttribute("staffList", staffList);
        model.addAttribute("staffs", staffList);
        long adminCount = staffList.stream().filter(s -> "ADMIN".equalsIgnoreCase(s.getRole())).count();
        long stockCount = staffList.stream().filter(s -> "STOCK".equalsIgnoreCase(s.getRole())).count();
        long userCount = staffList.stream().filter(s -> "USER".equalsIgnoreCase(s.getRole())).count();
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("stockCount", stockCount);
        model.addAttribute("userCount", userCount);
        return "admin/staff-list";
    }

    @GetMapping("/staff/new")
    public String newStaffForm(Model model) {
        model.addAttribute("staff", new Staff());
        return "admin/staff-form";
    }

    @PostMapping(value = "/staff/save", consumes = {"multipart/form-data", "application/x-www-form-urlencoded"})
    public String saveStaff(@ModelAttribute("staff") Staff staff,
            @RequestParam(value = "imageFile", required = false) org.springframework.web.multipart.MultipartFile imageFile,
            RedirectAttributes ra) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String originalFilename = imageFile.getOriginalFilename();
                String ext = ".jpg";
                if (originalFilename != null && originalFilename.contains(".")) {
                    ext = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String newFileName = "avatar_" + System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0, 8) + ext;

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

                staff.setImageUrl("/uploads/" + newFileName);
            } catch (Exception e) {
                // Keep existing URL if upload encounters an issue
            }
        }
        staffService.saveStaff(staff);
        ra.addFlashAttribute("successMessage", "Staff member '" + staff.getFullName() + "' saved successfully!");
        return "redirect:/admin/staff";
    }

    @GetMapping("/staff/edit/{id}")
    public String editStaffForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("staff", staffService.getStaffById(id));
        return "admin/staff-form";
    }

    @GetMapping("/staff/delete/{id}")
    public String deleteStaff(@PathVariable("id") Long id, RedirectAttributes ra) {
        staffService.deleteStaff(id);
        ra.addFlashAttribute("successMessage", "Staff member deleted successfully!");
        return "redirect:/admin/staff";
    }

    // Expired Products Report (Audit only, no CRUD)
    @GetMapping("/expired-products")
    public String expiredProducts(Model model) {
        List<Product> expired = productService.getExpiredProducts();
        model.addAttribute("expiredProducts", expired);
        return "admin/expired-products";
    }

    // Top Selling Categories Report
    @GetMapping("/top-categories")
    public String topCategories(Model model) {
        List<Object[]> topCats = saleService.getTopSellingCategories();
        model.addAttribute("topCategories", topCats);
        return "admin/top-categories";
    }

    // Sales History Report
    @GetMapping("/sales-history")
    public String salesHistory(Model model) {
        List<Sale> sales = saleService.getAllSales();
        model.addAttribute("sales", sales);
        model.addAttribute("salesList", sales);
        return "admin/sales-history";
    }

    // Payment History Report
    @GetMapping("/payment-history")
    public String paymentHistory(Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        model.addAttribute("paymentList", payments);
        model.addAttribute("paymentSummary", paymentService.getPaymentSummaryByMethod());
        return "admin/payment-history";
    }
}
