# FOSS Training Project

Open Source Physical Training Management System.

## Project Overview

**FOSS Training** is a free and open-source system designed for athletes, coaches, and strength enthusiasts to manage physical training routines, periodized programs, workout execution, sports science analytics, and athlete bodyweight tracking with full data sovereignty and zero vendor lock-in.

---

## Architecture & Structure

Monorepo containing backend REST API, frontend SPA, and local infrastructure:

```
foss-training/
├── docker-compose.yml   — Local PostgreSQL 17 database service
├── foss-training-api/   — Backend REST API (Spring Boot, JDK 26, PostgreSQL, Liquibase)
├── foss-training-web/   — Frontend SPA (Angular 22, TypeScript, Vitest, Tailwind/CSS)
└── README.md            — Project guide and launch instructions
```

### 1. Database (PostgreSQL 17 & Liquibase)
- Manages schema migrations via Liquibase (`db.changelog-master.yaml`).
- Relational schema covering exercises, sessions, training programs, workout execution logs, and athlete bodyweight records.

### 2. Backend REST API (`foss-training-api/`)
- Hexagonal architecture (Ports & Adapters) separating pure Java domain models from framework adapters.
- Spring Boot with **JDK 26** (managed via `mise`), JPA/Hibernate, and Springdoc OpenAPI.
- Interactive Swagger UI documentation served at `/swagger-ui.html`.

### 3. Frontend SPA (`foss-training-web/`)
- Modern **Angular 22** standalone components, signal reactivity, and Apple-inspired dark glassmorphism design system.
- Feature modules: Exercise Catalog, Workout Session Templates, Live Workout Execution Tracker, Periodized Programs, Sports Science Analytics (1RM, ACWR, Hypertrophy Volume, Heart Rate Zones), and Athlete Profile (Bodyweight Tracker, Relative Strength DOTS/Wilks Calculator, Data Sovereignty Export/Restore).

---

## Prerequisites

Before launching the project, ensure you have the following tools installed:

- **Docker & Docker Compose**: For running the PostgreSQL 17 database locally.
- **Java 26 (JDK 26)**: Managed via [`mise`](https://mise.jdx.dev/) (`mise use java@26.0.2` or installed at `~/.local/share/mise/installs/java/26.0.2`).
- **Node.js 20+ & npm 11+**: For building and running the Angular frontend.

---

## Getting Started: Launching the Applications

You can launch the complete stack (Database, Backend API, and Frontend SPA) locally in three simple steps.

### Step 1: Start the Database (PostgreSQL)

From the project root directory, run:

```bash
docker compose up -d
```

- **Service**: PostgreSQL 17 Alpine container (`foss-training-postgres`)
- **Port**: `localhost:5432`
- **Database**: `foss_training`
- **Credentials**: `postgres` / `postgres`

To verify the database is ready:
```bash
docker compose exec postgres pg_isready
```

### Step 2: Start the Backend REST API

In a new terminal window, navigate to `foss-training-api` and run Spring Boot with Java 26:

```bash
cd foss-training-api
JAVA_HOME=$(mise where java) ./mvnw spring-boot:run
```

- **API Base URL**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI v3 Docs**: `http://localhost:8080/v3/api-docs`

Liquibase will automatically execute database migrations on startup and initialize all required tables.

### Step 3: Start the Frontend Web Client

In another terminal window, navigate to `foss-training-web`, install dependencies (if first time), and start the Angular development server:

```bash
cd foss-training-web
npm install
npm start
```

- **Frontend App**: `http://localhost:4200`
- The application will automatically open or be accessible in your web browser.

---

## Stopping the Services

To stop the running services:

1. **Frontend / Backend**: Press `Ctrl + C` in their respective terminal windows.
2. **Database**: From the project root directory, run:
   ```bash
   docker compose down
   ```
   *(Add `-v` if you wish to wipe the persistent database volume: `docker compose down -v`)*

---

## Running Tests

### Backend Unit & Integration Tests

```bash
cd foss-training-api
JAVA_HOME=$(mise where java) ./mvnw test
```

### Frontend Tests (Vitest)

```bash
cd foss-training-web
npm test -- --watch=false    # Single run
npm test                     # Interactive watch mode
```

### Frontend Production Build

```bash
cd foss-training-web
npm run build
```
