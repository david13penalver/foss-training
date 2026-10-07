# Orchestrator Memory — FOSS Training

Index of operational learnings, cross-platform workflows, and milestone tracking.

## Initial Knowledge
- **Monorepo Structure**:
  - `foss-training-api`: Spring Boot, Java 26, PostgreSQL 17, Liquibase
  - `foss-training-web`: Angular 22, TypeScript, Tailwind CSS, Vitest
  - `foss-training-ios`: Swift 6, SwiftUI, SwiftData, Swift Testing
- **API Port & URL**: Spring Boot runs on `http://localhost:8080`, Swagger UI at `/swagger-ui.html`.
- **Frontend Port**: Angular SPA runs on `http://localhost:4200`.
- **Contract Synchronization**: Whenever backend DTOs change, run `npm run codegen:api` in `foss-training-web`.
- **Backup Portability**: iOS client SwiftData export/import is designed to sync with `/api/data/import/backup`.

---
<!-- Add entries with format:
- [name](file.md) — brief description
-->
