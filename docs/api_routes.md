# API & URL Route Specification

## 🚪 1. Authentication (`AuthController.java`)
| Method | URL | Description | Security |
|---|---|---|---|
| `GET` | `/` | Root entry point: Redirects guests & customers to `/shop` (Landing Page), `ADMIN` to `/admin`, and `STOCK` to `/stock` | Public |
| `GET` | `/login` | Render login form with quick demo account buttons & storefront link | Public |
| `POST` | `/login` | Authenticate credentials (`username`, `password`) and set `loggedInUser` session | Public |
| `GET` | `/register` / `/signup` | Render customer self-registration form (enforcing `USER` role only) | Public |
| `POST` | `/register` / `/signup` | Create customer account with unique username validation & auto-login | Public |
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
| `POST` | `/shop/wishlist/move-all-to-cart` | Transfer all in-stock wishlist items into shopping cart | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/wishlist/clear` | Clear all wishlist items (protected by Zero-JS confirmation modal `#clear-wishlist-modal`) | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/checkout` | Checkout view with payment method selection (`KHQR`, `CASH`, `CREDIT_CARD`) | `USER`, `ADMIN`, `STOCK` |
| `POST` | `/shop/checkout/process` | Submit order, validate stock, decrease inventory, record payment, and generate receipt | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/receipt/{id}` | Official printable order settlement receipt & tax invoice | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/orders` | Customer order history portal with lifetime analytics ribbon, purchased snack cards, & receipt links | `USER`, `ADMIN`, `STOCK` |

---

## ⚡ 5. RESTful Web Services & API Endpoints (Lesson 08 Implementation)

### A. Product REST Controller (`ProductRestController.java` $\rightarrow$ `/api/products`)
| Method | URL | Description | Request Body / Param | Response Status |
|---|---|---|---|---|
| `GET` | `/api/products` | Get all products | None | `200 OK` (JSON List) |
| `GET` | `/api/products/{id}` | Get product by ID | Path: `{id}` | `200 OK` / `404 Not Found` |
| `GET` | `/api/products/available` | Get active in-stock & unexpired products | None | `200 OK` (JSON List) |
| `GET` | `/api/products/expired` | Get expired products list | None | `200 OK` (JSON List) |
| `POST` | `/api/products` | Create new product | JSON `Product` | `201 CREATED` |
| `PUT` | `/api/products/{id}` | Update existing product | Path: `{id}`, JSON `Product` | `200 OK` / `404 Not Found` |
| `DELETE` | `/api/products/{id}` | Delete product by ID | Path: `{id}` | `204 NO CONTENT` / `404 Not Found` |

### B. Category REST Controller (`CategoryRestController.java` $\rightarrow$ `/api/categories`)
| Method | URL | Description | Request Body / Param | Response Status |
|---|---|---|---|---|
| `GET` | `/api/categories` | Get all snack categories | None | `200 OK` (JSON List) |
| `GET` | `/api/categories/{id}` | Get category by ID | Path: `{id}` | `200 OK` / `404 Not Found` |
| `POST` | `/api/categories` | Create new category | JSON `Category` | `201 CREATED` |
| `PUT` | `/api/categories/{id}` | Update category | Path: `{id}`, JSON `Category` | `200 OK` / `404 Not Found` |
| `DELETE` | `/api/categories/{id}` | Delete category by ID | Path: `{id}` | `204 NO CONTENT` / `404 Not Found` |

### C. Staff REST Controller (`StaffRestController.java` $\rightarrow$ `/api/staff`)
| Method | URL | Description | Request Body / Param | Response Status |
|---|---|---|---|---|
| `GET` | `/api/staff` | Get all staff & customer accounts | None | `200 OK` (JSON List) |
| `GET` | `/api/staff/{id}` | Get staff member by ID | Path: `{id}` | `200 OK` / `404 Not Found` |
| `POST` | `/api/staff` | Create new staff/user account | JSON `Staff` | `201 CREATED` |
| `PUT` | `/api/staff/{id}` | Update staff member | Path: `{id}`, JSON `Staff` | `200 OK` / `404 Not Found` |
| `DELETE` | `/api/staff/{id}` | Delete staff member by ID | Path: `{id}` | `204 NO CONTENT` / `404 Not Found` |

### D. Storefront Asynchronous REST Controller (`StoreRestController.java` $\rightarrow$ `/shop/api`)
| Method | URL | Description | Parameters | Response Status |
|---|---|---|---|---|
| `POST` | `/shop/api/cart/add` | Asynchronous add to cart | `productId`, `quantity` | `200 OK` / `400` / `401` |
| `POST` | `/shop/api/wishlist/toggle/{id}` | Asynchronous wishlist toggle | Path: `{id}` | `200 OK` / `401` |
| `GET` | `/shop/api/cart/count` | Real-time session cart count | None | `200 OK` (JSON) |
| `GET` | `/shop/api/wishlist/count` | Real-time session wishlist count | None | `200 OK` (JSON) |
