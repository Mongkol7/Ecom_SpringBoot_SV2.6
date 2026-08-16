package com.example.springboot_midterm.config;

import com.example.springboot_midterm.exception.UnauthorizedAccessException;
import com.example.springboot_midterm.model.Staff;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 1. Allow static resources & public endpoints
        if (uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/images/") ||
            uri.equals("/login") || uri.equals("/logout") || uri.equals("/error")) {
            return true;
        }

        // 2. Allow guest browsing for products only
        if (uri.equals("/") || uri.equals("/shop") || uri.equals("/shop/catalog") || uri.startsWith("/shop/product/")) {
            return true;
        }

        // 3. For all protected actions (cart, wishlist, checkout, orders, admin, stock), require login
        HttpSession session = request.getSession(false);
        Staff loggedInUser = session != null ? (Staff) session.getAttribute("loggedInUser") : null;

        if (loggedInUser == null) {
            String accept = request.getHeader("Accept");
            String requestedWith = request.getHeader("X-Requested-With");
            if (uri.startsWith("/shop/api/") || "XMLHttpRequest".equalsIgnoreCase(requestedWith) || (accept != null && accept.contains("application/json"))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"requireLogin\":true,\"message\":\"Please sign in to perform this action.\"}");
                return false;
            }
            response.sendRedirect("/login?required=true");
            return false;
        }

        // Prevent browser caching for authenticated views
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String role = loggedInUser.getRole();

        if (uri.startsWith("/admin") && !"ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("Access denied. Admin privileges required.");
        }

        if (uri.startsWith("/stock") && !"STOCK".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("Access denied. Stock Manager privileges required.");
        }

        return true;
    }
}
