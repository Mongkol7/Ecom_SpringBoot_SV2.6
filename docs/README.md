# 📚 Documentation & Memories Hub

Welcome to the central documentation and memories repository for **nitec. American Snacks E-Commerce Suite (Spring Boot Midterm)**.

---

## 📑 Table of Contents

| Document | Description |
| :--- | :--- |
| **[memories.md](file:///d:/Document/CODES%20DEV/SV2-Y3/JAVA%20Y4/SpringBoot_MidTerm/docs/memories.md)** | Core project memories, DB credentials, ports, RBAC policies, Zero-JS modals, and business rules |
| **[database_schema.md](file:///d:/Document/CODES%20DEV/SV2-Y3/JAVA%20Y4/SpringBoot_MidTerm/docs/database_schema.md)** | ER Diagram, PostgreSQL 18 table structures, relationships, and constraints |
| **[api_routes.md](file:///d:/Document/CODES%20DEV/SV2-Y3/JAVA%20Y4/SpringBoot_MidTerm/docs/api_routes.md)** | Complete HTTP endpoints across Auth, Admin, Stock, and Storefront controllers |
| **[DESIGN.md](file:///d:/Document/CODES%20DEV/SV2-Y3/JAVA%20Y4/SpringBoot_MidTerm/docs/DESIGN.md)** | Visual design guidelines, color tokens, typography, and component hierarchy |
| **[all_lesson.md](file:///d:/Document/CODES%20DEV/SV2-Y3/JAVA%20Y4/SpringBoot_MidTerm/docs/all_lesson.md)** | Course lessons, exam requirements, and grading criteria |
| **[demo_presentation_guide.md](file:///d:/Document/CODES%20DEV/SV2-Y3/JAVA%20Y4/SpringBoot_MidTerm/docs/demo_presentation_guide.md)** | Complete Live Demo script, Q&A answers, and file/folder summary |

---

## ⚡ Quick Reference
- **Running Port**: `3000` (`server.port=3000`)
- **Default Route**: `/` redirects guests and customers directly to the **Storefront Landing Page** (`/shop`).
- **Database Engine**: PostgreSQL 18 (`jdbc:postgresql://localhost:5432/E-bookstrore_db` / `Ecom-SpringBoot-SV26`, user: `postgres`, password: `123`).
- **Catalog Theme**: 16 Authentic American Snacks across 5 Categories (Chips & Crisps, Chocolates & Candies, Cookies & Pastries, Crackers & Pretzels, Jerky & Savory Snacks).
- **Default Accounts**:
  - `admin` / `123` (`ADMIN` Role)
  - `stock` / `123` (`STOCK` Role)
  - `user` / `123` (`USER` Role)
- **Key Modules**:
  - 🛡️ **Admin Suite**: Executive KPI dashboard, Staff directory CRUD, Loss analytics, and Sales audit logs.
  - 📦 **Stock Suite**: Unified `w-72` sidebar, live inventory CRUD, dual image upload/URL, and low stock / expiry alerts.
  - 🛍️ **Customer Storefront**: Top-sales ranked catalog, non-blocking AJAX cart & wishlist, live order receipt generation, and **"My Orders"** purchase history portal.
  - 🔒 **Zero-JS Confirmation Modals**: Pure CSS `:target` confirmation dialogs centered in the viewport for all deletions and sign-outs.
