package com.example.springboot_midterm.controller;

import com.example.springboot_midterm.model.*;
import com.example.springboot_midterm.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
        model.addAttribute("totalRevenue", totalRevenue);
        
        List<Sale> sales = saleService.getAllSales();
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

    @PostMapping("/staff/save")
    public String saveStaff(@ModelAttribute("staff") Staff staff, RedirectAttributes ra) {
        staffService.saveStaff(staff);
        ra.addFlashAttribute("successMessage", "Staff member saved successfully!");
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
