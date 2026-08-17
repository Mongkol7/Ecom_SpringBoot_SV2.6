# API & URL Route Specification

## 🚪 1. Authentication (`AuthController.java`)
| Method | URL | Description | Security |
|---|---|---|---|
| `GET` | `/` | Root entry point: Redirects guests & customers to `/shop` (Landing Page), `ADMIN` to `/admin`, and `STOCK` to `/stock` | Public |
| `GET` | `/login` | Render login form with quick demo account buttons & storefront link | Public |
| `POST` | `/login` | Authenticate credentials (`username`, `password`) and set `loggedInUser` session | Public |
| `GET`/`POST` | `/logout` | Invalidate active session, clear cache, and redirect to `/login?logout=true` | Authenticated |

---

## 🛡️ 2. Admin Portal (`AdminController.java`)
| Method | URL | Description | Access Role |
|---|---|---|---|
| `GET` | `/admin` | Executive KPI Dashboard (Gross Revenue, Expired Stock Loss, Net Realized Revenue, Active Inventory Valuation, Wastage Rate, Sales Breakdown) | `ADMIN` |
| `GET` | `/admin/staff` | Staff RBAC directory with real-time search, role filter tabs, and CSV export | `ADMIN` |
| `GET` | `/admin/staff/new` | Staff registration form with local image upload & URL avatar preview | `ADMIN` |
| `POST` | `/admin/staff/save` | Create or update staff account (`multipart/form-data`) | `ADMIN` |
| `GET` | `/admin/staff/edit/{id}` | Edit existing staff member form | `ADMIN` |
| `GET` | `/admin/staff/delete/{id}` | Delete staff account (protected by Zero-JS confirmation modal `#delete-staff-{id}`) | `ADMIN` |
| `GET` | `/admin/expired-products` | Inventory audit of expired products with financial loss calculation | `ADMIN` |
| `GET` | `/admin/top-categories` | Top-selling snack categories revenue & quantity analytics | `ADMIN` |
| `GET` | `/admin/sales-history` | Audit log of all completed customer orders & payment records | `ADMIN` |
| `GET` | `/admin/payment-history` | Transaction settlements ledger (`KHQR`, `CASH`, `CARD`) | `ADMIN` |

---

## 📦 3. Stock Management (`StockController.java`)
| Method | URL | Description | Access Role |
|---|---|---|---|
| `GET` | `/stock` | Stock operations dashboard (Low Stock ≤ 5 & Expiring < 1 Mo action alerts) | `STOCK`, `ADMIN` |
| `GET` | `/stock/products` | Inventory product catalog with toolbar quick filters (`all`, `expired`, `lowstock`), category filter, & keyword search | `STOCK`, `ADMIN` |
| `POST` | `/stock/products/quick-add/{id}` | Quick restock shortcut (+10 stock units) | `STOCK`, `ADMIN` |
| `GET` | `/stock/products/new` | Snack product creation form (Dual local upload / URL input with live image preview) | `STOCK`, `ADMIN` |
| `POST` | `/stock/products/save` | Create or update product (`multipart/form-data` image file / URL) | `STOCK`, `ADMIN` |
| `GET` | `/stock/products/edit/{id}` | Edit product details and inventory form | `STOCK`, `ADMIN` |
| `GET` | `/stock/products/delete/{id}` | Delete product item (protected by Zero-JS confirmation modal `#delete-product-{id}`) | `STOCK`, `ADMIN` |
| `GET` | `/stock/categories` | Categories split view (Category creation form + active category table with product counts) | `STOCK`, `ADMIN` |
| `POST` | `/stock/categories/save` | Save new or edited category | `STOCK`, `ADMIN` |
| `GET` | `/stock/categories/edit/{id}` | Edit category form | `STOCK`, `ADMIN` |
| `GET` | `/stock/categories/delete/{id}` | Delete category (protected by Zero-JS confirmation modal `#delete-cat-{id}`) | `STOCK`, `ADMIN` |

---

## 🛍️ 4. Customer Storefront (`UserController.java`)
| Method | URL | Description | Access Role |
|---|---|---|---|
| `GET` | `/shop` | American Snacks Storefront Landing Page sorted by Top Sales volume | Public / Customer |
| `GET` | `/shop/catalog` | Interactive snack catalog with category filter, price range slider, and live search | Public / Customer |
| `GET` | `/shop/product/{id}` | Snack product detail view with stock status and category-related snack suggestions | Public / Customer |
| `GET` | `/shop/cart` | Shopping cart review ledger with item increment/decrement actions | `USER`, `ADMIN`, `STOCK` |
| `POST` | `/shop/cart/add` | Add product to cart with stock validation and buyNow flag support | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/cart/update` | Update item quantity in shopping cart | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/cart/remove/{id}` | Remove item from cart (protected by Zero-JS confirmation modal `#remove-cart-{id}`) | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/wishlist` | Saved favorites collection with empty state and batch move-to-cart | `USER`, `ADMIN`, `STOCK` |
| `GET`/`POST` | `/shop/wishlist/toggle/{id}` | Standard HTTP toggle product in/out of session wishlist | `USER`, `ADMIN`, `STOCK` |
| `POST` | `/shop/api/wishlist/toggle/{id}` | Non-blocking AJAX toggle returning JSON payload with updated count & heart state | `USER`, `ADMIN`, `STOCK` |
| `POST` | `/shop/wishlist/move-all-to-cart` | Transfer all in-stock wishlist items into shopping cart | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/wishlist/clear` | Clear all wishlist items (protected by Zero-JS confirmation modal `#clear-wishlist-modal`) | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/checkout` | Checkout view with payment method selection (`KHQR`, `CASH`, `CREDIT_CARD`) | `USER`, `ADMIN`, `STOCK` |
| `POST` | `/shop/checkout/process` | Submit order, validate stock, decrease inventory, record payment, and generate receipt | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/receipt/{id}` | Official printable order settlement receipt & tax invoice | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/orders` | Customer order history portal with lifetime analytics ribbon, purchased snack cards, & receipt links | `USER`, `ADMIN`, `STOCK` |
