---
name: Luminous Monochrome (Snack E-Commerce Edition)
colors:
  surface: '#fcf9f8'
  surface-dim: '#dcd9d9'
  surface-bright: '#fcf9f8'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f3f2'
  surface-container: '#f0edec'
  surface-container-high: '#ebe7e7'
  surface-container-highest: '#e5e2e1'
  on-surface: '#1c1b1b'
  on-surface-variant: '#444933'
  inverse-surface: '#313030'
  inverse-on-surface: '#f3f0ef'
  outline: '#747a60'
  outline-variant: '#c4c9ac'
  surface-tint: '#506600'
  primary: '#506600'
  on-primary: '#ffffff'
  primary-container: '#ccff00'
  on-primary-container: '#5b7300'
  inverse-primary: '#abd600'
  secondary: '#5d5f5f'
  on-secondary: '#ffffff'
  secondary-container: '#dfe0e0'
  on-secondary-container: '#616363'
  tertiary: '#5b5f61'
  on-tertiary: '#ffffff'
  tertiary-container: '#ebedf0'
  on-tertiary-container: '#686b6e'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#c3f400'
  primary-fixed-dim: '#abd600'
  on-primary-fixed: '#161e00'
  on-primary-fixed-variant: '#3c4d00'
  secondary-fixed: '#e2e2e2'
  secondary-fixed-dim: '#c6c6c7'
  on-secondary-fixed: '#1a1c1c'
  on-secondary-fixed-variant: '#454747'
  tertiary-fixed: '#e0e3e6'
  tertiary-fixed-dim: '#c4c7ca'
  on-tertiary-fixed: '#191c1e'
  on-tertiary-fixed-variant: '#44474a'
  background: '#fcf9f8'
  on-background: '#1c1b1b'
  surface-variant: '#e5e2e1'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.01em
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-bold:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '700'
    lineHeight: 20px
    letterSpacing: 0.05em
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  unit: 4px
  container-padding: 32px
  element-gap: 16px
  section-margin: 64px
  gutter: 24px
---

## Brand & Style

This design system embodies a futuristic, hyper-refined aesthetic that merges the depth of Neumorphism with the clarity of "Glassmorphism." It is engineered for a premium American snack e-commerce retail and stock management portal.

The style leverages **Soft Glassmorphism** to create a sense of lightness and transparency, while **Neumorphic** principles are applied via monochromatic tonal layering to provide structural weight and tactile feedback.

## Colors & Hierarchy

The palette is rooted in a sophisticated high-contrast monochrome foundation, punctuated by vibrant accents:

- **Primary Accent (Lime Green `#CCFF00`):** Reserved for primary calls-to-action, success states, and brand highlights against `#111111` off-black.
- **Alert Colors (Red `#EF4444` & Amber `#F59E0B`):** Used for expiration warnings, low-stock alerts, and urgent inventory badges.
- **Canvas (`#F3F5F8`):** Soft modern background providing high contrast for pure white (`#FFFFFF`) raised cards.
- **Glass Overlays:** Applied to floating navigation headers and modals (`rgba(255, 255, 255, 0.75)` with `backdrop-filter: blur(16px)`).

## Typography

- **Font Family:** `Plus Jakarta Sans` across all headings, body text, buttons, and numeric values.
- **Headlines:** Tight letter-spacing with extra-bold weights (`font-extrabold` / `font-bold`).
- **Numbers & Prices:** High legibility bold numerals (e.g. `$4.99`, `45 units`).

## Layout & Product Imagery

- **Main Content Clearance:** All admin, stock, and user views maintain `pt-[96px] pb-12 px-6 md:px-8` to prevent overlap with the fixed `64px` (`h-16`) navbar.
- **Product Card Image Frames:** Standardized **1:1 `aspect-square`** white containers with `object-contain max-h-full max-w-full` to display authentic packaging photography with zero distortion or awkward clipping.
- **Shapes:**
  - Floating Island Containers: `28px` (`rounded-[28px]`).
  - Inner Action Cards: `20px` to `24px` (`rounded-2xl` / `rounded-[24px]`).
  - Buttons, Filters & Search Bars: Fully rounded pill shape (`rounded-full`).