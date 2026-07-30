# FOSS Training — Agent Guide

## Project structure

Single-module Maven project at `foss-training-api/`. Future modules (frontend/KMP, DB scripts) not yet present.

## Build & test

```sh
./mvnw clean test              # full test suite
./mvnw test -Dtest=ExerciseTest  # single test class
```

Java 21 target (JDK 26 runtime), Spring Boot 3.4.4, Maven wrapper (`mvnw`).

## Architecture (Hexagonal / Ports & Adapters)

```
domain/          — pure Java, no framework annotations
  model/         — entities (Exercise, Session, metrics, enums)
  ports/in/      — inbound port interfaces (e.g., ExerciseService)
  ports/out/     — outbound port interfaces (e.g., ExerciseRepository)
application/     — depends on domain only
  usecases/      — single-action interfaces + impls, each with execute()
  services/      — implement domain ports/in interfaces
infrastructure/  — framework glue (Spring, JPA, etc.)
  adapters/in/rest/   — @RestController classes
  adapters/out/       — @Repository impls (currently stubs)
  configuration/      — @Configuration beans (currently empty)
```

**Conventions**: Use cases are interfaces in `usecases/<entity>/` with impls under `usecases/<entity>/impl/`. Controllers inject use case interfaces (not services directly). Domain layer must not import Spring/DB annotations.

## Quirks & gotchas

- **Test packages** use `package unitary.com.david13penalver...` (not the standard `src/test/java/com/...` convention).
- **Repository adapters** are stubs returning empty/noop values (no JPA or real persistence wired yet).
- **Lombok** is used extensively (`@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@RequiredArgsConstructor`, `@Slf4j`). Needs annotation processor configured in IDE. Lombok version is explicitly overridden to 1.18.46 in `pom.xml` for JDK 26 compatibility.
- **application.properties** only has `spring.application.name`; most config (DB connection, etc.) is absent.
- **No CI, formatter, linter, or typecheck config** exists.

## Commands

```sh
./mvnw spring-boot:run    # start dev server
./mvnw test -Dtest=*Test  # all tests matching pattern
```

## Domain model notes

Three exercise categories with corresponding metrics:
- `ResistanceMetrics` (movement pattern, muscle groups, reps/sets/weight/tempo)
- `EnduranceMetrics` (endurance type, blocks, distance/pace/heart rate/power)
- `MobilityMetrics` (mobility/stretch type, target joints, hold time, timing)

All use `Integer` IDs. Soft-delete via `isActive` flag on `Exercise`.
