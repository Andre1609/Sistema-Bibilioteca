# Product

<!-- impeccable:product-schema 1 -->

## Platform

Java 21 desktop application using Swing/AWT.

## Users

- Library administrators and librarians manage the catalog, loans, returns, students, fines, and reports.
- Students consult the catalog and their loans and fines.

## Product Purpose

Operate the library's day-to-day circulation and catalog workflows while giving students self-service access to their library information. Success means preserving these working operations while making them easier to understand and use.

## Capabilities and Constraints

- PostgreSQL persistence through JDBC and the existing DAO layer.
- Preserve role-based navigation and existing book, loan, return, user, student, fine, report, and catalog operations.
- Do not change the database schema or existing SQL without a documented compatibility plan.
- Keep database credentials out of versioned source code.
- The project is also an academic demonstration; document only patterns and data structures exercised by working application flows.

## Brand Commitments

- Preserve the current product name and the existing UTP logo.
- Improve the logo's presentation without altering its identity.

## Evidence on Hand

- The role-based workflows are implemented in `src/vista/PanelPrincipal.java`.
- The existing logo is `src/recursos/UTP.png`.
- The product's confirmed operations and technical constraints come from the user's project brief.

## Product Principles

1. Preserve existing library workflows and role boundaries.
2. Make common staff operations clear and efficient.
3. Give students direct access to their catalog and account information.
4. Apply design patterns only where a working workflow benefits from them.
5. Keep credentials and technical errors out of user-facing content.

## Accessibility & Inclusion

Provide legible contrast, clear labels, visible keyboard focus, and keyboard-operable controls.
