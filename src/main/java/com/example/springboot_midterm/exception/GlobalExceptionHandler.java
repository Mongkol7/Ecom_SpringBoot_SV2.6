package com.example.springboot_midterm.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model, HttpServletRequest request) {
        if (isRestRequest(request)) {
            throw ex; // Handled by REST advice or fallback
        }
        model.addAttribute("status", 404);
        model.addAttribute("error", "Not Found");
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(InsufficientStockException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInsufficientStock(InsufficientStockException ex, Model model, HttpServletRequest request) {
        model.addAttribute("status", 400);
        model.addAttribute("error", "Insufficient Stock");
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleUnauthorizedAccess(UnauthorizedAccessException ex, Model model, HttpServletRequest request) {
        model.addAttribute("status", 403);
        model.addAttribute("error", "Access Denied");
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model, HttpServletRequest request) {
        model.addAttribute("status", 500);
        model.addAttribute("error", "Internal Server Error");
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    private boolean isRestRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/");
    }
}
