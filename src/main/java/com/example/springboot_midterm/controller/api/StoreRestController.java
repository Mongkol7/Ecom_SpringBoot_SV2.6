package com.example.springboot_midterm.controller.api;

import com.example.springboot_midterm.model.Product;
import com.example.springboot_midterm.model.Staff;
import com.example.springboot_midterm.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/shop/api")
public class StoreRestController {

    private final ProductService productService;

    public StoreRestController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart(@RequestParam("productId") Long productId,
                                       @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                                       HttpSession session) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "requireLogin", true, "message", "Please log in before adding items to your cart."));
        }

        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        Product product = productService.getProductById(productId);
        if (product.isExpired()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Cannot add expired product: " + product.getPName()));
        }

        int currentQtyInCart = cart.getOrDefault(productId, 0);
        if (currentQtyInCart + quantity > product.getSQty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Insufficient stock for " + product.getPName() + ". Only " + product.getSQty() + " available."));
        }

        cart.put(productId, currentQtyInCart + quantity);
        session.setAttribute("cart", cart);

        int totalCartCount = getCartItemCount(session);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Successfully added \"" + product.getPName() + "\" to your cart!",
                "cartCount", totalCartCount,
                "productName", product.getPName()
        ));
    }

    @PostMapping("/wishlist/toggle/{id}")
    public ResponseEntity<?> toggleWishlist(@PathVariable("id") Long id, HttpSession session) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "requireLogin", true, "message", "Please log in to save items to your wishlist."));
        }

        @SuppressWarnings("unchecked")
        Set<Long> wishlist = (Set<Long>) session.getAttribute("wishlist");
        if (wishlist == null) {
            wishlist = new HashSet<>();
        }

        Product product = productService.getProductById(id);
        boolean inWishlist;
        String message;
        if (wishlist.contains(id)) {
            wishlist.remove(id);
            inWishlist = false;
            message = "Removed \"" + product.getPName() + "\" from wishlist.";
        } else {
            wishlist.add(id);
            inWishlist = true;
            message = "Saved \"" + product.getPName() + "\" to wishlist!";
        }

        session.setAttribute("wishlist", wishlist);
        int totalWishlistCount = wishlist.size();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "inWishlist", inWishlist,
                "wishlistCount", totalWishlistCount,
                "message", message,
                "productName", product.getPName()
        ));
    }

    @GetMapping("/cart/count")
    public ResponseEntity<Map<String, Integer>> getCartCount(HttpSession session) {
        return ResponseEntity.ok(Map.of("cartCount", getCartItemCount(session)));
    }

    @GetMapping("/wishlist/count")
    public ResponseEntity<Map<String, Integer>> getWishlistCount(HttpSession session) {
        @SuppressWarnings("unchecked")
        Set<Long> wishlist = (Set<Long>) session.getAttribute("wishlist");
        int count = (wishlist != null) ? wishlist.size() : 0;
        return ResponseEntity.ok(Map.of("wishlistCount", count));
    }

    private int getCartItemCount(HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        return cart != null ? cart.values().stream().mapToInt(val -> val != null ? val : 0).sum() : 0;
    }
}
