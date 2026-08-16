# API & URL Route Specification

## 🚪 1. Authentication (`AuthController.java`)
| Method | URL | Description | Security |
|---|---|---|---|
| `GET` | `/login` | Render login form | Public |
| `POST` | `/login` | Process user credential authentication | Public |
| `GET`/`POST` | `/logout` | Invalidate active session and redirect to `/login?logout=true` | Authenticated |

---

## 🛡️ 2. Admin Portal (`AdminController.java`)
| Method | URL | Description | Access Role |
|---|---|---|---|
| `GET` | `/admin` | System overview dashboard with KPI stats & revenue charts | `ADMIN` |
| `GET` | `/admin/staff` | Staff RBAC directory with live search & CSV export | `ADMIN` |
| `GET` | `/admin/staff/new` | Render staff registration form | `ADMIN` |
| `POST` | `/admin/staff/save` | Create or update staff account | `ADMIN` |
| `GET` | `/admin/staff/edit/{id}` | Render staff edit form | `ADMIN` |
| `GET` | `/admin/staff/delete/{id}` | Remove staff member | `ADMIN` |
| `GET` | `/admin/expired-products` | List of all expired products | `ADMIN` |
| `GET` | `/admin/top-categories` | Top-selling categories revenue report | `ADMIN` |
| `GET` | `/admin/sales-history` | Audit log of all completed customer orders | `ADMIN` |
| `GET` | `/admin/payment-history` | Transaction settlements log | `ADMIN` |

---

## 📦 3. Stock Management (`StockController.java`)
| Method | URL | Description | Access Role |
|---|---|---|---|
| `GET` | `/stock` | Stock overview metrics (Low stock ≤ 5 & Expiring < 1 Mo alerts) | `STOCK`, `ADMIN` |
| `GET` | `/stock/products` | Inventory product list with `filter` (`expired`/`lowstock`), `catId`, & `keyword` | `STOCK`, `ADMIN` |
| `POST` | `/stock/products/quick-add/{id}` | Quick restock shortcut (`amount=10`) | `STOCK`, `ADMIN` |
| `GET` | `/stock/products/new` | Add new product form (Dual local upload / URL input) | `STOCK`, `ADMIN` |
| `POST` | `/stock/products/save` | Save product item (`multipart/form-data` image file / URL) | `STOCK`, `ADMIN` |
| `GET` | `/stock/products/edit/{id}` | Edit product form | `STOCK`, `ADMIN` |
| `GET` | `/stock/products/delete/{id}` | Delete product item | `STOCK`, `ADMIN` |
| `GET` | `/stock/categories` | Categories list CRUD with linked product counts | `STOCK`, `ADMIN` |
| `POST` | `/stock/categories/save` | Save category | `STOCK`, `ADMIN` |
| `GET` | `/stock/categories/edit/{id}` | Edit category form | `STOCK`, `ADMIN` |
| `GET` | `/stock/categories/delete/{id}` | Delete category | `STOCK`, `ADMIN` |

---

## 🛍️ 4. Customer Storefront (`UserController.java`)
| Method | URL | Description | Access Role |
|---|---|---|---|
| `GET` | `/` | Root redirect to `/shop` | Public |
| `GET` | `/shop` | American Snacks landing page sorted by Top Sales volume | `USER`, `ADMIN`, `STOCK`, Public |
| `GET` | `/shop/catalog` | Filterable catalog with search, price slider, and categories | `USER`, `ADMIN`, `STOCK`, Public |
| `GET` | `/shop/product/{id}` | Product detail view with stock status and category-related snacks | `USER`, `ADMIN`, `STOCK`, Public |
| `GET` | `/shop/cart` | Shopping cart review ledger | `USER`, `ADMIN`, `STOCK` |
| `POST` | `/shop/cart/add` | Add product to cart with quantity validation (AJAX) | `USER`, `ADMIN`, `STOCK` |
| `POST` | `/shop/cart/update` | Update item quantity in cart | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/cart/remove/{id}` | Remove item from shopping cart | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/wishlist` | Wishlist view | `USER`, `ADMIN`, `STOCK` |
| `POST` | `/shop/wishlist/toggle` | Add/remove product from wishlist (AJAX) | `USER`, `ADMIN`, `STOCK` |
| `POST` | `/shop/wishlist/move-all-to-cart` | Transfer all wishlist items to cart | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/wishlist/clear` | Clear all wishlist items | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/checkout` | Checkout screen with payment method selection (`KHQR`, `CASH`, `CREDIT_CARD`) | `USER`, `ADMIN`, `STOCK` |
| `POST` | `/shop/checkout/process` | Submit purchase and create non-cancellable invoice | `USER`, `ADMIN`, `STOCK` |
| `GET` | `/shop/orders` | Customer order invoice history | `USER`, `ADMIN`, `STOCK` |
