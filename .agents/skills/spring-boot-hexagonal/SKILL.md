---
name: spring-boot-hexagonal
description: Guidelines and architectural conventions for backend development with Java 26, Spring Boot, Hexagonal Architecture (Ports & Adapters), Liquibase, and PostgreSQL in foss-training-api.
---

# Spring Boot Hexagonal Architecture Guidelines

Standard operating guide for backend development in `foss-training-api`.

---

## 1. Environment & Commands

Always use Java 26 via `mise`:

```bash
# Full test suite
JAVA_HOME=$(mise where java) ./mvnw clean test

# Single test class
JAVA_HOME=$(mise where java) ./mvnw test -Dtest=ExerciseTest

# Run backend dev server (port 8080)
JAVA_HOME=$(mise where java) ./mvnw spring-boot:run
```

- **OpenAPI / Swagger UI:** `http://localhost:8080/swagger-ui.html` and `http://localhost:8080/v3/api-docs`
- **Docker PostgreSQL:** `docker compose up -d` (PostgreSQL 17 on localhost:5432)

---

## 2. Hexagonal Architecture (Ports & Adapters)

Directory structure under `foss-training-api/src/main/java/com/david13penalver/foss_training_api/`:

```
├── domain/                      — Pure Java, zero framework/Spring/DB annotations
│   ├── model/                   — Aggregate entities, value objects, domain enums
│   └── ports/out/               — Outbound port interfaces (e.g. ExerciseRepository)
├── application/                 — Application orchestration (depends on domain only)
│   └── usecases/<entity>/       — Inbound use case interfaces (e.g. SaveExerciseUseCase)
│       └── impl/                — Service implementations (e.g. SaveExerciseService)
└── infrastructure/              — Framework adapters & configuration
    ├── adapters/in/rest/        — @RestController, DTOs, mappers, GlobalExceptionHandler
    ├── adapters/out/
    │   ├── persistence/jpa/     — Spring Data JPA repositories, JPA entities, persistence mappers
    │   ├── <entity>/            — Active repository adapters (*RepositoryImpl)
    │   └── <entity>/InMemory*Dao.java — [DEPRECATED] Legacy in-memory DAOs for migration simulation
    └── configuration/           — Spring @Configuration beans (Jackson, OpenAPI)
```

### Strict Boundary Rules
1. **Zero Framework Annotations in Domain**: Classes in `domain/` must NEVER import `org.springframework.*`, `jakarta.persistence.*`, `org.hibernate.*`, or `com.fasterxml.jackson.*`.
2. **Ports Define Contracts**: Outbound ports in `domain/ports/out/` declare the interface methods needed by the domain/application.
3. **Use Cases in Application**: Application services in `application/usecases/*/impl/` orchestrate domain entities and call outbound ports. They may use `@Service` and `@Transactional` (Spring Framework allowed in application layer).
4. **DTO Decoupling in Infrastructure**: REST DTOs in `infrastructure/adapters/in/rest/dto/` isolate wire format from domain models. Use explicit mapper classes (e.g. `ExerciseDtoMapper`) to transform between DTOs and domain aggregates.
5. **In-Memory DAOs**: The `InMemory*Dao` classes are deprecated and retained solely for educational migration simulation. Do not delete them. Active repositories (`*RepositoryImpl`) delegate to Spring Data JPA.

---

## 3. Database Migrations (Liquibase)

- Master changelog: `src/main/resources/db/changelog/db.changelog-master.yaml`
- Incremental changelogs: `src/main/resources/db/changelog/changes/*.yaml`
- Rules:
  - All database schema updates must be declared via Liquibase YAML changesets.
  - Test suites execute against an in-memory H2 database in PostgreSQL mode (`MODE=PostgreSQL`) running these changelogs automatically.
  - Foreign keys, unique constraints, and indices must be explicitly configured.

---

## 4. DTO Validation & Serialization Conventions

- **Validation**: Use Jakarta Bean Validation (`@Valid`, `@NotBlank`, `@NotNull`, `@Min`, `@Max`, `@Size`).
- **OpenAPI Schema**: Annotate DTOs with `@Schema` for accurate OpenAPI generation.
- **Enums**:
  - Serialize using `name()` constant strings.
  - Deserialization with `@JsonCreator fromString(String value)` must be case-insensitive, return `null` for null input, and throw `IllegalArgumentException` for invalid values.
- **Polymorphic Types**: Use `@JsonTypeInfo` and `@JsonSubTypes` when handling polymorphic categories (e.g., `ResistanceMetricsDto`, `EnduranceMetricsDto`, `MobilityMetricsDto`).

---

## 5. Testing Standards

- **Package Prefix**: Test classes reside under `package unitary.com.david13penalver.foss_training_api...`.
- **Spring Boot Tests**: For tests loading Spring context, explicitly set `@SpringBootTest(classes = FossTrainingApiApplication.class)`.
- **JDK 26 Mockito Constraints**: Mockito inline mocks cannot instrument classes with `synchronized` methods on JDK 26. Favor interface mocking or real domain instances.
- **Coverage**: Maintain high unit test coverage on domain calculation services, use cases, and persistence mappers.
