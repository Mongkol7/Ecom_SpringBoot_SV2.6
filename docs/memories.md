# Project Memories & Operational Rules

## 📌 Environment & Server Configuration

- **Server Port**: `3000` (Defined in `application.properties`: `server.port=3000`).
- **Database Engine**: PostgreSQL 18.
- **Database Name**: `E-bookstrore_db` (or `Ecom-SpringBoot-SV26`).
- **Database User**: `postgres`.
- **Database Password**: `123`.
- **Database URL**: `jdbc:postgresql://localhost:5432/E-bookstrore_db`.
- **JPA / Hibernate DDL**: `update` with formatted SQL logging enabled.

---

## 🔐 Authentication & Session Architecture

- **Session Keys**:
  - `loggedInUser` -> `com.example.springboot_midterm.model.Staff` (Roles: `ADMIN`, `STOCK`, `USER`).
  - `cart` -> `Map<Long, Integer>` (ProductId to Quantity mapping).
  - `wishlist` -> `Set<Long>` (Set of ProductIds).
- **Session Invalidation**:
  - `/logout` explicitly executes `session.invalidate()` on both `GET` and `POST` methods.
  - Browser cache prevention headers are enforced via `AuthInterceptor` (`Cache-Control: no-cache, no-store, must-revalidate`).
- **Role-Based Access Control (RBAC)**:
  - `ADMIN`: Dedicated to `/admin/**` (Executive Dashboard, Staff Accounts RBAC CRUD, Sales Invoices Audit, Payment Records, Top Performing Categories, and Expired Inventory Audit). All Admin views strictly share the unified `adminSidebar` and `adminTopbar` components with no raw Product CRUD linkages and no storefront button in navigation.
  - `STOCK`: Dedicated access to `/stock/**` for Inventory, Product, and Category CRUD. Clean top navbar (no search, bell, or dots).
  - `USER`: Customer access to `/shop/**`, catalog, product details, cart, wishlist, and checkout.

---

## 📦 Stock Module Architecture (`/stock`)

1. **Dashboard Overview (`/stock`)**:
   - **4 KPI Cards**: Total Products, Active Categories, Low Stock (`sQty <= 5`), and Expiring Soon (`< 1 Month` or Expired).
   - **Dual Action Alert Panels**:
     - *Low Stock Alert Panel*: Immediate restock shortcuts (`+10` units 1-click button).
     - *Expiring Shelf-Life Panel*: Items nearing expiration with 1-click `Renew Date` modal.
2. **Product Management (`/stock/products`)**:
   - **Toolbar Quick Filters**:
     - `All Products` button with total catalog count badge.
     - `Expired Product` button with expiring count badge (filters and sorts from earliest expiry date first).
     - `Low Stock` button with low stock count badge (filters and sorts from lowest quantity first).
     - `Category` dropdown filter & keyword search input.
     - `Reset` shortcut button when filters are active.
   - Clean empty state without intrusive create buttons.
3. **Product Creation / Edit Form (`/stock/products/new`)**:
   - **Dual Image Input**:
     - Tab 1: *Select Image from Local Drive* (handles multipart file upload to `/uploads/`).
     - Tab 2: *Paste Image Link* (web URL or static path).
   - **No Default Forced Image**: Displays clean placeholder until user selects/pastes an image.
   - **Real-Time Live Preview**: Instantly updates preview frame with file name and size details.
4. **Category Management (`/stock/categories`)**:
   - Split view: Quick Category Creation Card + Categories Inventory Table with live linked products count.

---

## 🛒 Storefront & E-Commerce Business Logic (`/shop`)

1. **American Snacks Catalog**:
   - Initial database seeds 16 authentic American Snack products (Doritos, Cheetos, Lay's, Pringles, Reese's, Hershey's, M&M's, Sour Patch Kids, Oreo, Pop-Tarts, Twinkies, Goldfish, Cheez-It, Snyder's Pretzels, Jack Link's Beef Jerky) across 5 categories with high-resolution photography.
2. **Landing Page (`/shop`)**:
   - **Top Sales Ranking**: Products on homepage showcase are ranked by highest total sold quantity (`findTopSellingProducts()`).
   - **Standardized Image Frames**: Contained `aspect-square` white cards with `object-contain` for perfectly centered, uncropped packages.
   - **Direct Click Navigation**: Clicking product packaging images or titles redirects directly to `/shop/product/{id}`.
3. **Related Products**:
   - Products on detail view strictly show items within the **same category**, excluding the active product.
4. **Order & Payment Settlement**:
   - Non-cancellable finalized invoices.
   - 1-to-1 normalized `Payment` entity (`KHQR`, `CASH`, `CREDIT_CARD`).
   - Checking out clears the cart while maintaining user session.
5. **Non-Blocking AJAX Interactions**:
   - Add to Cart: AJAX request with animated toast alert (`#toastNotification`) and navbar counter update.
   - Wishlist Toggle: AJAX request with animated toast alert and heart icon state fill.
   - Guest User Policy: Guests can browse only. Cart, Wishlist, or Checkout triggers an interactive Login Prompt Modal.

---

## 🎨 UI & Design System

- **Palette**: Luminous Monochrome (#F3F5F8 background, #FFFFFF raised cards, #111111 off-black, and #CCFF00 lime green accents).
- **Typography**: Plus Jakarta Sans.
- **Top Margin Rule**: Main content uses `pt-[96px] pb-12 px-6 md:px-8` to ensure a consistent 32px breathing room below the fixed 64px navbar.
