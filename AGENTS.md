# FOSS Training — Agent Guide

## Project structure

Monorepo containing backend REST API, frontend SPA, and local infrastructure:

```
foss-training/
├── foss-training-api/   — Backend REST API (Spring Boot, JDK 26, PostgreSQL, Liquibase)
├── foss-training-web/   — Frontend SPA (Angular 22, TypeScript, Vitest, Tailwind/CSS)
├── foss-training-ios/   — Native iOS app (Swift 6, SwiftUI, SwiftData, Swift Testing)
├── docker-compose.yml   — Local PostgreSQL 17 database service
├── .agents/             — Multi-platform skills, specialized subagents, and persistent memory
└── AGENTS.md            — Agent architectural guide and developer conventions
```

---

## Build, Test & Run

### Prerequisites & Runtimes
- **Java 26 (JDK 26)**: Managed via `mise` (located at `~/.local/share/mise/installs/java/26.0.2`). When running Maven commands, always prefix with `JAVA_HOME=$(mise where java)` or `mise exec --`.
- **Node.js / npm**: Node 20+, npm 11.
- **Swift 6 / Xcode**: Target iOS 26+ (on macOS with Xcode 16+ / XcodeGen).
- **Docker**: For running PostgreSQL locally.

### Backend (`foss-training-api/`)

```sh
# Run tests (requires Java 26)
JAVA_HOME=$(mise where java) ./mvnw clean test                 # full test suite
JAVA_HOME=$(mise where java) ./mvnw test -Dtest=ExerciseTest   # single test class

# Start backend dev server (port 8080)
JAVA_HOME=$(mise where java) ./mvnw spring-boot:run

# OpenAPI docs & Swagger UI:
# http://localhost:8080/swagger-ui.html
# http://localhost:8080/v3/api-docs
```

### Frontend (`foss-training-web/`)

```sh
cd foss-training-web
npm install

# Run frontend tests with Vitest
npm test -- --watch=false       # single run
npm test                        # watch mode

# Build & Serve (port 4200)
npm run build
npm start                       # ng serve -> http://localhost:4200

# Regenerate API TypeScript types from OpenAPI spec
npm run codegen:api
```

### Mobile iOS (`foss-training-ios/`)

```sh
cd foss-training-ios

# Run tests via xcodebuild (on macOS)
xcodebuild -project FOSSTraining.xcodeproj -scheme FOSSTraining -destination 'generic/platform=iOS Simulator' test
```

### Infrastructure (Docker)

```sh
docker compose up -d            # starts PostgreSQL 17 on localhost:5432
docker compose down
```

---

## Backend Architecture (Hexagonal / Ports & Adapters)

```
foss-training-api/src/main/java/com/david13penalver/foss_training_api/
├── domain/                      — Pure Java, zero framework/Spring/DB annotations
│   ├── model/                   — Aggregate entities & value objects (exercise, session, training, program, analytics, enums)
│   └── ports/out/               — Outbound port interfaces (ExerciseRepository, SessionRepository, TrainingRepository, TrainingProgramRepository)
├── application/                 — Application orchestration (depends on domain only)
│   └── usecases/<entity>/       — Inbound use case interfaces (e.g., SaveExerciseUseCase, StartTrainingUseCase)
│       └── impl/                — Service implementations (e.g., SaveExerciseService, StartTrainingService)
└── infrastructure/              — Framework adapters & configuration
    ├── adapters/in/rest/        — @RestController classes, request/response DTOs, mappers, GlobalExceptionHandler
    ├── adapters/out/
    │   ├── persistence/jpa/     — Active persistence layer: Spring Data JPA repositories, JPA entities, persistence mappers
    │   ├── <entity>/            — Active repository adapters (*RepositoryImpl) delegating to Spring Data JPA
    │   └── <entity>/InMemory*Dao.java — [DEPRECATED] Legacy in-memory DAOs retained for educational migration simulation
    └── configuration/           — Spring @Configuration beans (Jackson, OpenAPI)
```

### Educational Migration Simulation (In-Memory DAOs)
> [!NOTE]
> The in-memory DAO classes (`InMemoryExerciseDao`, `InMemorySessionDao`, `InMemoryTrainingDao`, `InMemoryTrainingProgramDao`) are **deprecated** and intentionally retained in the codebase for **educational purposes**. They simulate an architectural migration path from an initial in-memory prototype system to a production-grade Spring Data JPA + PostgreSQL + Liquibase infrastructure.
> - Active persistence repository implementations (`*RepositoryImpl`) delegate to Spring Data JPA repositories (`SpringData*Repository`).
> - Do not delete or remove the `InMemory*Dao` classes.

### Backend Conventions
- **Hexagonal Boundaries**: Domain model and application use cases must never import Spring, JPA, Hibernate, or Jackson classes.
- **DTOs Decouple Domain from Wire Format**: DTOs in `infrastructure/adapters/in/rest/dto/` carry Bean Validation (`@Valid`, `@NotBlank`, `@NotNull`), Jackson annotations (`@JsonTypeInfo`, `@JsonSubTypes`), and OpenAPI schemas (`@Schema`). Mappers (`ExerciseDtoMapper`, `SessionDtoMapper`, etc.) translate between DTOs and pure domain entities.
- **Database Migrations**: Liquibase manages schema changes via `src/main/resources/db/changelog/` (`db.changelog-master.yaml`). All tables use primary keys, foreign keys, and audit/state tracking.

---

## Frontend Architecture (`foss-training-web`)

- **Angular 22**: Modern standalone component architecture (`standalone: true`), signal-based reactivity (`signal`, `computed`, `effect`, `input`, `output`), and modern control flow (`@if`, `@for`, `@switch`).
- **State & Service Layer**: Core services (`ExerciseService`, `SessionService`, `TrainingService`, `ReferenceDataService`, `ProgramService`, `AnalyticsService`) manage reactive state using Angular signals and communicate with the REST API.
- **Routes & Features**:
  - `/exercises`: Exercise catalog with category filters, muscle group tags, and search.
  - `/sessions`: Session template builder and exercise sequencer.
  - `/trainings`: Scheduled and active workout execution tracker with status transitions.
  - `/programs`: Multi-week training program planner and schedule generator.
  - `/analytics`: Volume tracking, personal record milestones, and 1RM calculators.
- **UI Design System**: Dark-themed, glassmorphic UI with translucent panels, responsive modals, animated status badges, and accessible form controls.
- **Testing**: Unit and integration testing powered by Vitest and `@angular/build` using jsdom.

---

## iOS Native Architecture (`foss-training-ios`)

- **Swift 6 & Strict Concurrency**: Configured with `SWIFT_STRICT_CONCURRENCY: complete`, targeting iOS 26+. ViewModels are isolated to `@MainActor`, and data crossing actor boundaries conforms to `Sendable`.
- **Architecture**: MVVM + Hexagonal Ports & Adapters.
  - **Domain Layer**: Pure Swift with zero framework dependencies (no SwiftUI, no SwiftData).
  - **Ports (Protocols)**: `ExerciseRepository`, `SessionRepository`, `TrainingRepository`, `AthleteRepository`, `DataPortabilityRepository`.
  - **Adapters**: Local-first `SwiftData*Repository` (on-device `ModelContext`) and `Remote*Repository` (Spring Boot REST API).
- **Theming Invariant**: Theme settings (accent color, OLED Pure Black, surface style) must be stored in `UserDefaults` / `@AppStorage` via `ThemeManager`, never in SwiftData (ensures zero theme flash on launch).
- **Testing**: Modern Swift Testing (`import Testing`, `@Suite`, `@Test`, `#expect`) with hermetic in-memory SwiftData containers (`ModelConfiguration(isStoredInMemoryOnly: true)`).

---

## Agent Ecosystem (`.agents/`)

The `.agents/` directory provides specialized subagents, procedural skills, and persistent memory configured specifically for FOSS Training:

### Specialized Subagents (`.agents/subagents/`)
- `orchestrator`: Cross-platform feature coordinator across Backend, Web, and iOS.
- `planner`: Technical specification and task planner (`plan.md`, `tasks.md`).
- `backend_architect`: Java 26, Spring Boot, Hexagonal Architecture, JPA, Liquibase, OpenAPI.
- `frontend_angular_specialist`: Angular 22, Signals, Tailwind glassmorphic UI, Vitest.
- `ios_tdd_architect`: Swift 6, SwiftUI, SwiftData, Swift Testing, strict concurrency.
- `sports_science_domain_agent`: 1RM calculations, ACWR, DOTS/Wilks, mathematical parity.
- `reviewer`: Multi-platform quality, architectural boundary, and test gatekeeper.

### Specialized Skills (`.agents/skills/`)
- `spring-boot-hexagonal`: Spring Boot + Java 26 hexagonal patterns and testing rules.
- `angular-best-practices`: Angular 22 standalone components, signals reactivity, and Vitest.
- `swift-swiftui-best-practices`: Swift 6 concurrency, SwiftUI `@Observable`, SwiftData.
- `sports-science-parity`: Canonical definitions and parity checks for 1RM, ACWR, and DOTS/Wilks.
- `tdd-workflow`: Red-Green-Refactor workflows tailored for Java, Angular, and Swift.
- `spec-generator`: Feature specification interview and EARS-compliant requirements generation.
- `accessibility`: WCAG 2.2 web guidelines and iOS Dynamic Type / VoiceOver patterns.
- `playwright-cli`: Web browser automation and E2E testing for Angular SPA.

---

## Quirks & Gotchas

- **Java 26 Runtime via `mise`**: The host environment's default `java` command may point to JDK 21, while the project requires JDK 26. Always execute Maven commands with `JAVA_HOME=$(mise where java) ./mvnw ...`.
- **Test Packages (`package unitary.com.david13penalver...`)**: Backend test classes reside under `unitary.com.david13penalver...` rather than standard root packages. Tests must explicitly import classes under test. Tests requiring Spring context use `@SpringBootTest(classes = FossTrainingApiApplication.class)` because Spring cannot auto-discover configuration across package prefixes.
- **Test Database (H2 + Liquibase)**: Test executions use an in-memory H2 database running in PostgreSQL compatibility mode (`MODE=PostgreSQL`). Liquibase changelogs execute automatically against H2 during test startup, validating migrations against real DDL.
- **Mockito Inline on JDK 26**: Mockito inline mocks cannot instrument classes with `synchronized` methods on JDK 26. Favor testing repository interfaces with mocks or testing using real instances.
- **Enum Serialization & Deserialization**:
  - Enums serialize as their constant `name()` string by default.
  - Deserialization uses `@JsonCreator fromString(String value)`. If `value == null`, it returns `null` to accommodate optional fields. Case-insensitive matching maps string names and human-readable names; unrecognized non-null strings throw `IllegalArgumentException`.
- **Lombok Version Override**: Lombok is explicitly configured to version `1.18.46` in `pom.xml` to maintain compatibility with javac on JDK 26.
- **Vitest in CI / Non-Watch Mode**: When running frontend tests in non-interactive environments, pass `--watch=false` (i.e. `npm test -- --watch=false`).

---

## Domain Model & Aggregates

1. **Exercise**:
   - Categories: `RESISTANCE` (`ResistanceMetrics`), `ENDURANCE` (`EnduranceMetrics`), `MOBILITY` (`MobilityMetrics`).
   - Soft-delete supported via `isActive` flag.
2. **Session**:
   - Reusable workout template defining ordered exercise list (`SessionExercise`), target metrics, rest periods, and estimated duration.
3. **Training**:
   - Realized execution of a workout session.
   - Status lifecycle: `SCHEDULED` ➔ `IN_PROGRESS` ➔ `COMPLETED` or `CANCELLED`.
   - Records actual start/end timestamps, RPE (1-10 exertion), session notes, and logged exercise metrics.
4. **TrainingProgram**:
   - Multi-week training cycle with target goal, difficulty level, and scheduled workout days mapping to sessions.
5. **Analytics**:
   - Personal record tracking, weekly/monthly training volume calculations, and 1RM estimation formulas (Brzycki, Epley, Lander, etc.).
