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
