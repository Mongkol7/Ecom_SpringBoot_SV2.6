package com.example.springboot_midterm.controller;

import com.example.springboot_midterm.model.Staff;
import com.example.springboot_midterm.service.StaffService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final StaffService staffService;

    public AuthController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("/")
    public String index(HttpSession session) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        if (user != null) {
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                return "redirect:/admin";
            } else if ("STOCK".equalsIgnoreCase(user.getRole())) {
                return "redirect:/stock";
            }
        }
        return "redirect:/shop";
    }

    @GetMapping("/login")
    public String showLoginForm(HttpSession session, Model model) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        if (user != null) {
            return redirectByRole(user.getRole());
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpSession session,
                               Model model) {
        try {
            Staff staff = staffService.authenticate(username, password);
            session.setAttribute("loggedInUser", staff);
            return redirectByRole(staff.getRole());
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "login";
        }
    }

    @GetMapping({"/register", "/signup"})
    public String showRegisterForm(HttpSession session, Model model) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        if (user != null) {
            return redirectByRole(user.getRole());
        }
        return "register";
    }

    @PostMapping({"/register", "/signup"})
    public String processRegister(@RequestParam("fullName") String fullName,
                                  @RequestParam("username") String username,
                                  @RequestParam("password") String password,
                                  @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                                  @RequestParam(value = "imageUrl", required = false) String imageUrl,
                                  HttpSession session,
                                  Model model,
                                  org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        String trimmedUsername = (username != null) ? username.trim() : "";
        String trimmedFullName = (fullName != null) ? fullName.trim() : "";

        model.addAttribute("fullName", trimmedFullName);
        model.addAttribute("username", trimmedUsername);
        model.addAttribute("imageUrl", imageUrl);

        if (trimmedUsername.isEmpty()) {
            model.addAttribute("error", "Username is required.");
            return "register";
        }

        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Password is required.");
            return "register";
        }

        if (password.length() < 3) {
            model.addAttribute("error", "Password must be at least 3 characters long.");
            return "register";
        }

        if (confirmPassword != null && !confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match. Please verify your password.");
            return "register";
        }

        if (staffService.findByUserName(trimmedUsername).isPresent()) {
            model.addAttribute("error", "Username '" + trimmedUsername + "' is already taken. Please choose another username.");
            return "register";
        }

        // Strictly enforce USER role only for customer registration
        Staff newCustomer = new Staff();
        newCustomer.setFullName(!trimmedFullName.isEmpty() ? trimmedFullName : trimmedUsername);
        newCustomer.setUserName(trimmedUsername);
        newCustomer.setPassword(password);
        newCustomer.setRole("USER"); // Hardcoded USER role

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            newCustomer.setImageUrl(imageUrl.trim());
        }

        Staff savedStaff = staffService.saveStaff(newCustomer);
        session.setAttribute("loggedInUser", savedStaff);
        ra.addFlashAttribute("successMessage", "Welcome to nitec. American Snacks, " + savedStaff.getFullName() + "! Your account was created successfully.");
        return "redirect:/shop";
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login?logout=true";
    }

    private String redirectByRole(String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return "redirect:/admin";
        } else if ("STOCK".equalsIgnoreCase(role)) {
            return "redirect:/stock";
        } else {
            return "redirect:/shop";
        }
    }
}
