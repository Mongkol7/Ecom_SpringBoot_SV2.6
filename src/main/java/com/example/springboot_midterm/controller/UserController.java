package com.example.springboot_midterm.controller;

import com.example.springboot_midterm.model.Product;
import com.example.springboot_midterm.model.Sale;
import com.example.springboot_midterm.model.Staff;
import com.example.springboot_midterm.service.CategoryService;
import com.example.springboot_midterm.service.ProductService;
import com.example.springboot_midterm.service.SaleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/shop")
public class UserController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SaleService saleService;

    public UserController(ProductService productService, CategoryService categoryService, SaleService saleService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.saleService = saleService;
    }

    @ModelAttribute("user")
    public Staff getLoggedInUser(HttpSession session) {
        return (Staff) session.getAttribute("loggedInUser");
    }

    @ModelAttribute("wishlist")
    public Set<Long> getWishlist(HttpSession session) {
        @SuppressWarnings("unchecked")
        Set<Long> wishlist = (Set<Long>) session.getAttribute("wishlist");
        return (wishlist != null) ? wishlist : Collections.emptySet();
    }

    @GetMapping
    public String browseProducts(@RequestParam(value = "keyword", required = false) String keyword,
                                 Model model,
                                 HttpSession session) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        List<Product> products = new java.util.ArrayList<>(productService.searchAndFilterAvailableProducts(keyword, null));
        java.util.Map<Long, Long> salesMap = saleService.getProductSalesQuantityMap();

        // Sort products by top sales (highest sold quantity first)
        products.sort((p1, p2) -> {
            Long s1Val = (p1 != null && p1.getPId() != null && salesMap != null) ? salesMap.get(p1.getPId()) : null;
            Long s2Val = (p2 != null && p2.getPId() != null && salesMap != null) ? salesMap.get(p2.getPId()) : null;
            long s1 = (s1Val != null) ? s1Val : 0L;
            long s2 = (s2Val != null) ? s2Val : 0L;
            if (s1 != s2) {
                return Long.compare(s2, s1);
            }
            if (p1 != null && p2 != null && p1.getPId() != null && p2.getPId() != null) {
                return Long.compare(p1.getPId(), p2.getPId());
            }
            return 0;
        });

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("wishlist", session.getAttribute("wishlist"));

        model.addAttribute("cartCount", getCartItemCount(session));
        return "user/shop";
    }

    @GetMapping("/catalog")
    public String catalog(@RequestParam(value = "keyword", required = false) String keyword,
                          @RequestParam(value = "catId", required = false) Long catId,
                          Model model,
                          HttpSession session) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        List<Product> products = productService.searchAndFilterAvailableProducts(keyword, catId);
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCatId", catId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("wishlist", session.getAttribute("wishlist"));

        model.addAttribute("cartCount", getCartItemCount(session));
        return "user/catalog";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model, HttpSession session) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        List<Product> available = productService.getAvailableProducts();
        List<Product> relatedProducts = new java.util.ArrayList<>();
        if (product != null && product.getCategory() != null) {
            Long currentCatId = product.getCategory().getCatId();
            relatedProducts = available.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getCatId().equals(currentCatId))
                    .filter(p -> !p.getPId().equals(id))
                    .limit(4)
                    .collect(java.util.stream.Collectors.toList());
        }
        if (relatedProducts.size() < 4) {
            List<Product> finalRelated = relatedProducts;
            List<Product> others = available.stream()
                    .filter(p -> !p.getPId().equals(id))
                    .filter(p -> finalRelated.stream().noneMatch(r -> r.getPId().equals(p.getPId())))
                    .limit(4 - relatedProducts.size())
                    .collect(java.util.stream.Collectors.toList());
            relatedProducts.addAll(others);
        }
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("wishlist", session.getAttribute("wishlist"));

        model.addAttribute("cartCount", getCartItemCount(session));
        return "user/product-detail";
    }

    @GetMapping("/wishlist")
    public String wishlist(Model model, HttpSession session) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login?required=true";
        }
        model.addAttribute("user", user);

        @SuppressWarnings("unchecked")
        Set<Long> wishlist = (Set<Long>) session.getAttribute("wishlist");
        List<Product> savedProducts;
        boolean hasSavedItems = (wishlist != null && !wishlist.isEmpty());

        if (wishlist != null && !wishlist.isEmpty()) {
            savedProducts = wishlist.stream()
                    .map(id -> {
                        try { return productService.getProductById(id); } catch (Exception e) { return null; }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            savedProducts = new ArrayList<>();
        }

        model.addAttribute("products", savedProducts);
        model.addAttribute("hasSavedItems", hasSavedItems);
        model.addAttribute("cartCount", getCartItemCount(session));
        return "user/wishlist";
    }

    @RequestMapping(value = "/wishlist/toggle/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String toggleWishlist(@PathVariable("id") Long id,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        if (user == null) {
            ra.addFlashAttribute("errorMessage", "Please log in to save items to your wishlist.");
            return "redirect:/login?required=true";
        }
        @SuppressWarnings("unchecked")
        Set<Long> wishlist = (Set<Long>) session.getAttribute("wishlist");
        if (wishlist == null) {
            wishlist = new HashSet<>();
        }

        Product product = productService.getProductById(id);
        if (wishlist.contains(id)) {
            wishlist.remove(id);
            ra.addFlashAttribute("successMessage", "Removed " + product.getPName() + " from wishlist.");
        } else {
            wishlist.add(id);
            ra.addFlashAttribute("successMessage", "Saved " + product.getPName() + " to wishlist!");
        }

        session.setAttribute("wishlist", wishlist);
        return "redirect:/shop/wishlist";
    }

    @PostMapping("/wishlist/move-all-to-cart")
    public String moveAllWishlistToCart(HttpSession session, RedirectAttributes ra) {
        @SuppressWarnings("unchecked")
        Set<Long> wishlist = (Set<Long>) session.getAttribute("wishlist");
        if (wishlist == null || wishlist.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "No saved items to move to cart.");
            return "redirect:/shop/wishlist";
        }

        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        int movedCount = 0;
        for (Long prodId : wishlist) {
            try {
                Product p = productService.getProductById(prodId);
                if (!p.isExpired() && p.getSQty() > 0) {
                    cart.put(prodId, cart.getOrDefault(prodId, 0) + 1);
                    movedCount++;
                }
            } catch (Exception ignored) {}
        }

        session.setAttribute("cart", cart);
        ra.addFlashAttribute("successMessage", "Moved " + movedCount + " in-stock items to your cart!");
        return "redirect:/shop/cart";
    }

    @GetMapping("/wishlist/clear")
    public String clearWishlist(HttpSession session, RedirectAttributes ra) {
        session.removeAttribute("wishlist");
        ra.addFlashAttribute("successMessage", "Wishlist cleared.");
        return "redirect:/shop/wishlist";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                            @RequestParam(value = "buyNow", defaultValue = "false") boolean buyNow,
                            HttpSession session,
                            RedirectAttributes ra) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        if (user == null) {
            ra.addFlashAttribute("errorMessage", "Please log in before adding items to your cart.");
            return "redirect:/login?required=true";
        }

        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        Product product = productService.getProductById(productId);
        if (product.isExpired()) {
            ra.addFlashAttribute("errorMessage", "Cannot add expired product: " + product.getPName());
            return "redirect:/shop/catalog";
        }

        int currentQtyInCart = cart.getOrDefault(productId, 0);

        if (currentQtyInCart + quantity > product.getSQty()) {
            ra.addFlashAttribute("errorMessage", "Insufficient stock for " + product.getPName() + ". Only " + product.getSQty() + " available.");
            return "redirect:/shop/catalog";
        }

        cart.put(productId, currentQtyInCart + quantity);
        session.setAttribute("cart", cart);

        if (buyNow) {
            return "redirect:/shop/checkout";
        }

        ra.addFlashAttribute("successMessage", "Successfully added \"" + product.getPName() + "\" (Qty: " + quantity + ") to your cart!");
        return "redirect:/shop/cart";
    }

    @PostMapping("/api/cart/add")
    @ResponseBody
    public ResponseEntity<?> apiAddToCart(@RequestParam("productId") Long productId,
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

    @PostMapping("/api/wishlist/toggle/{id}")
    @ResponseBody
    public ResponseEntity<?> apiToggleWishlist(@PathVariable("id") Long id, HttpSession session) {
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

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login?required=true";
        }
        model.addAttribute("user", user);

        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        List<CartItemDTO> items = new ArrayList<>();
        double grandTotal = 0.0;

        if (cart != null) {
            for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
                Product p = productService.getProductById(entry.getKey());
                double subtotal = p.getPrice() * entry.getValue();
                grandTotal += subtotal;
                items.add(new CartItemDTO(p, entry.getValue(), subtotal));
            }
        }

        model.addAttribute("cartItems", items);
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("cartCount", getCartItemCount(session));
        return "user/cart";
    }

    @RequestMapping(value = "/cart/update", method = {RequestMethod.GET, RequestMethod.POST})
    public String updateCart(@RequestParam("productId") Long productId,
                             @RequestParam("quantity") Integer quantity,
                             HttpSession session,
                             RedirectAttributes ra) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart != null) {
            Product product = productService.getProductById(productId);
            if (quantity <= 0) {
                cart.remove(productId);
                ra.addFlashAttribute("warningMessage", "Removed \"" + product.getPName() + "\" from your cart.");
            } else {
                if (quantity > product.getSQty()) {
                    cart.put(productId, product.getSQty());
                    ra.addFlashAttribute("warningMessage", "Adjusted quantity to max available in stock (" + product.getSQty() + ").");
                } else {
                    cart.put(productId, quantity);
                    ra.addFlashAttribute("successMessage", "Updated quantity for \"" + product.getPName() + "\" to " + quantity + ".");
                }
            }
            session.setAttribute("cart", cart);
        }
        return "redirect:/shop/cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable("id") Long id, HttpSession session, RedirectAttributes ra) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart != null && cart.containsKey(id)) {
            try {
                Product product = productService.getProductById(id);
                cart.remove(id);
                ra.addFlashAttribute("warningMessage", "Removed \"" + product.getPName() + "\" from your shopping cart.");
            } catch (Exception e) {
                cart.remove(id);
            }
            session.setAttribute("cart", cart);
        }
        return "redirect:/shop/cart";
    }

    @GetMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return "redirect:/shop/cart";
    }

    @GetMapping("/checkout")
    public String showCheckout(HttpSession session, Model model) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login?required=true";
        }
        model.addAttribute("user", user);

        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/shop/catalog";
        }

        List<CartItemDTO> items = new ArrayList<>();
        double grandTotal = 0.0;
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product p = productService.getProductById(entry.getKey());
            double subtotal = p.getPrice() * entry.getValue();
            grandTotal += subtotal;
            items.add(new CartItemDTO(p, entry.getValue(), subtotal));
        }

        model.addAttribute("cartItems", items);
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("cartCount", getCartItemCount(session));
        return "user/checkout";
    }

    @PostMapping("/checkout/process")
    public String processCheckout(@RequestParam("paymentMethod") String paymentMethod,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        if (user == null) {
            ra.addFlashAttribute("errorMessage", "Please log in to complete checkout.");
            return "redirect:/login?required=true";
        }

        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Cart is empty.");
            return "redirect:/shop/catalog";
        }

        try {
            Sale completedSale = saleService.processPurchase(user, cart, paymentMethod);
            session.removeAttribute("cart");
            ra.addFlashAttribute("completedSale", completedSale);
            return "redirect:/shop/receipt/" + completedSale.getSaleId();
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/shop/checkout";
        }
    }

    @GetMapping("/receipt/{id}")
    public String showReceipt(@PathVariable("id") Long id, Model model, HttpSession session) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login?required=true";
        }
        model.addAttribute("user", user);
        Sale sale = saleService.getSaleById(id);
        model.addAttribute("sale", sale);
        model.addAttribute("cartCount", getCartItemCount(session));
        return "user/receipt";
    }

    @GetMapping("/orders")
    public String orderHistory(HttpSession session, Model model) {
        Staff user = (Staff) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login?required=true";
        }
        model.addAttribute("user", user);
        List<Sale> mySales = saleService.getSalesByCustomer(user.getSId());
        model.addAttribute("sales", mySales);

        double totalSpent = (mySales != null) ? mySales.stream()
                .mapToDouble(s -> (s != null && s.getTotalAmount() != null) ? s.getTotalAmount() : 0.0)
                .sum() : 0.0;
        int totalItemsPurchased = (mySales != null) ? mySales.stream()
                .flatMap(s -> (s != null && s.getDetails() != null) ? s.getDetails().stream() : java.util.stream.Stream.empty())
                .mapToInt(d -> (d != null && d.getQuantity() != null) ? d.getQuantity() : 0)
                .sum() : 0;
        int totalOrders = (mySales != null) ? mySales.size() : 0;

        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalItemsPurchased", totalItemsPurchased);
        model.addAttribute("cartCount", getCartItemCount(session));
        return "user/orders";
    }

    @ModelAttribute("cartCount")
    public int populateCartCount(HttpSession session) {
        return getCartItemCount(session);
    }

    @ModelAttribute("wishlistCount")
    public int populateWishlistCount(HttpSession session) {
        return getWishlistCount(session);
    }

    private int getCartItemCount(HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("cart");
        return cart != null ? cart.values().stream().mapToInt(val -> val != null ? val : 0).sum() : 0;
    }

    private int getWishlistCount(HttpSession session) {
        @SuppressWarnings("unchecked")
        Set<Long> wishlist = (Set<Long>) session.getAttribute("wishlist");
        return wishlist != null ? wishlist.size() : 0;
    }

    // Helper DTO for Cart View
    public static class CartItemDTO {
        private final Product product;
        private final Integer quantity;
        private final Double subtotal;

        public CartItemDTO(Product product, Integer quantity, Double subtotal) {
            this.product = product;
            this.quantity = quantity;
            this.subtotal = subtotal;
        }

        public Product getProduct() { return product; }
        public Integer getQuantity() { return quantity; }
        public Double getSubtotal() { return subtotal; }
    }
}
