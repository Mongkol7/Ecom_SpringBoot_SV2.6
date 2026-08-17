# Project Memories & Operational Rules

## 📌 Environment & Server Configuration

- **Server Port**: `3000` (Defined in `application.properties`: `server.port=3000`).
- **Database Engine**: PostgreSQL 18.
- **Database Name**: `E-bookstrore_db` (or `Ecom-SpringBoot-SV26`).
- **Database User**: `postgres`.
- **Database Password**: `123`.
- **Database URL**: `jdbc:postgresql://localhost:5432/E-bookstrore_db`.
- **JPA / Hibernate DDL**: `update` with formatted SQL logging enabled.
- **Hibernate 6.x JPQL Standard**: Field references in `@Query` must match the exact camelCase Java entity field name (e.g. `s.customer.sId` rather than `c.SId`).

---

## 🚪 Default Route & Entry Point

- **Root URL (`/`)**:
  - Unauthenticated visitors and standard customers navigating to `http://localhost:3000/` are routed directly to the **Storefront Landing Page** (`/shop`).
  - Authenticated `ADMIN` users visiting `/` are directed to `/admin`.
  - Authenticated `STOCK` users visiting `/` are directed to `/stock`.
- **Login Page (`/login`)**:
  - Features quick demo account buttons (`ADMIN`, `STOCK`, `USER`) and a **"Continue to Snack Storefront"** shortcut link.

---

## 🔐 Authentication, Session & RBAC Architecture

- **Session Keys**:
  - `loggedInUser` -> `com.example.springboot_midterm.model.Staff` (Roles: `ADMIN`, `STOCK`, `USER`).
  - `cart` -> `Map<Long, Integer>` (ProductId to Quantity mapping).
  - `wishlist` -> `Set<Long>` (Set of ProductIds).
- **Session Invalidation**:
  - `/logout` explicitly executes `session.invalidate()` on both `GET` and `POST` methods.
  - Browser cache prevention headers are enforced via `AuthInterceptor` (`Cache-Control: no-cache, no-store, must-revalidate`).
- **Role-Based Access Control (RBAC)**:
  - `ADMIN`: Dedicated to `/admin/**` (Executive Dashboard featuring Gross Revenue, Expired Stock Loss calculations, Net Realized Revenue, Sellable Inventory Valuation, Wastage Rate, Staff RBAC CRUD, Sales Invoices Audit, Payment Records, Top Performing Categories, and Expired Inventory Audit). All Admin views strictly share the unified `adminSidebar` and `adminTopbar` components.
  - `STOCK`: Dedicated access to `/stock/**` for Inventory, Product, and Category CRUD. Standardized to exact matching layout, dimensions, and styling as the Admin portal.
  - `USER`: Customer access to `/shop/**`, catalog, product details, cart, wishlist, checkout, and order history.

---

## 🛡️ Pure CSS Zero-JS Confirmation Modals Architecture

All delete and logout actions are protected by **Pure CSS `:target` Modals (No JavaScript)**:
1. **Logout Confirmation**: `#logout-modal` (Admin sidebar, Stock sidebar, Storefront navbar).
2. **Staff Deletion**: `#delete-staff-{id}` (Admin staff directory).
3. **Product Deletion**: `#delete-product-{id}` (Stock product inventory).
4. **Category Deletion**: `#delete-cat-{id}` (Stock category list).
5. **Cart Item Removal**: `#remove-cart-{id}` (Shopping cart ledger).
6. **Clear Entire Wishlist**: `#clear-wishlist-modal` (Wishlist collection).

> [!IMPORTANT]
> **CSS Containing Block Rule**:
> Elements with `backdrop-filter`, `transform`, or `overflow` create a new containing block that traps `position: fixed` children.
> All modal markup is placed at the **root viewport level** (outside `<nav>`, `<aside>`, `<table>`, and `.glass-panel` containers) with `z-[999]` and `animate-scale-up` to ensure every modal pops up directly in the dead center of the screen with a full-page overlay.

---

## 📐 Sidebar & Navbar Harmonization (Admin & Stock)

- **Sidebar Dimensions & Style**:
  - Fixed `w-72 h-full min-h-screen p-6 border-r border-white/40 bg-white/65 dark:bg-white/10 backdrop-blur-lg z-50`.
  - User profile header with circular `w-12 h-12` avatar (local/URL support, letter fallback monogram), name, and role badge.
  - Navigation links: `px-4 py-3 rounded-xl gap-3 text-sm` with `#CCFF00` active indicator.
  - Bottom action CTA: `#111111` off-black pill with `#CCFF00` accent text (*"New Staff Member"* / *"New Snack Product"*).
- **Topbar (`adminTopbar` & `stockTopbar`)**:
  - Fixed `h-16 top-0 right-0 left-0 md:left-72 px-6 md:px-8 border-b border-white/40 backdrop-blur-xl z-40`.
  - Brand titles: `nitec. Executive Suite` and `nitec. Stock Operations`.
  - Status pill: Avatar thumbnail + pulsing emerald dot + username + role tag.
- **Canvas Containers**:
  - Outer wrapper: `<div class="flex-1 md:pl-72 flex flex-col w-full min-h-screen">`
  - Main container: `<main class="flex-1 pt-24 px-5 md:px-8 pb-12 max-w-7xl mx-auto w-full">`

---

## 📦 Stock Module Architecture (`/stock`)

1. **Dashboard Overview (`/stock`)**:
   - **4 KPI Cards**: Total Products, Active Categories, Low Stock (`sQty <= 5`), and Expiring Soon (`< 1 Month` or Expired).
   - **Dual Action Alert Panels**: Immediate restock shortcuts (`+10` units 1-click button) and Expiring Shelf-Life alerts.
2. **Product Management (`/stock/products`)**:
   - **Toolbar Quick Filters**: `All Products`, `Expired Product` (earliest expiry first), `Low Stock` (lowest quantity first), `Category` dropdown, and keyword search.
3. **Product Creation / Edit Form (`/stock/products/new`)**:
   - **Dual Image Input**: Tab 1 (Local file upload to `/uploads/`) and Tab 2 (Paste web image URL) with real-time live preview.
4. **Category Management (`/stock/categories`)**:
   - Split view: Quick Category Creation Card + Categories Inventory Table with live linked products count.

---

## 🛍️ Storefront & E-Commerce Business Logic (`/shop`)

1. **American Snacks Catalog**:
   - Initial database seeds 16 authentic American Snack products (Doritos, Cheetos, Lay's, Pringles, Reese's, Hershey's, M&M's, Sour Patch Kids, Oreo, Pop-Tarts, Twinkies, Goldfish, Cheez-It, Snyder's Pretzels, Jack Link's Beef Jerky) across 5 categories with high-resolution photography.
2. **Landing Page (`/shop`)**:
   - **Top Sales Ranking**: Products on homepage showcase are ranked by highest total sold quantity (`findTopSellingProducts()`).
   - **Standardized Image Frames**: Contained `aspect-square` white cards with `object-contain` for perfectly centered, uncropped packages.
   - **Direct Click Navigation**: Clicking product packaging images or titles redirects directly to `/shop/product/{id}`.
3. **Wishlist Collection (`/shop/wishlist`)**:
   - Stores saved products in session `wishlist` (`Set<Long>`).
   - Returns empty list when empty (never falls back to entire product catalog).
   - Dedicated Empty State view with prompt and *"Explore Snack Catalog"* CTA button.
   - Global `@ModelAttribute("wishlist")` and `@ModelAttribute("wishlistCount")` keep navbar badge and heart icons synchronized.
4. **Customer Order History (`/shop/orders`)**:
   - Customer orders view displaying lifetime analytics (Total Orders, Lifetime Spent, Snack Units Purchased).
   - Card timeline displaying Order ID, formatted timestamp, transaction reference, payment pill (`KHQR`, `CASH`, `CARD`), and glowing status badge.
   - Detailed snack breakdowns with packaging thumbnails, product names, quantities, and subtotals.
   - Direct links to official tax invoice receipts (`/shop/receipt/{id}`).
5. **Navbar Actions**:
   - Storefront top navbar includes: Cart button, Wishlist button, **My Orders button** (right side of Wishlist), and User Auth pill.
   - Guest visitors clicking Cart, Wishlist, or Orders trigger an interactive Login Prompt Modal.
6. **Order & Payment Settlement**:
   - Non-cancellable finalized invoices.
   - 1-to-1 normalized `Payment` entity (`KHQR`, `CASH`, `CREDIT_CARD`).
   - Checking out clears the cart while maintaining user session.

---

## 🎨 UI & Design System

- **Palette**: Luminous Monochrome (#F3F5F8 background, #FFFFFF raised cards, #111111 off-black, and #CCFF00 lime green accents).
- **Typography**: Plus Jakarta Sans (`.material-symbols-outlined { font-family: 'Material Symbols Outlined' !important; }`).
- **Top Margin Rule**: Main content uses `pt-24 pb-12 px-5 md:px-8` to ensure a consistent breathing room below the fixed navbar.
