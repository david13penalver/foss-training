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
  ports/out/     — outbound port interfaces (e.g., ExerciseRepository)
application/     — depends on domain only
  usecases/      — single-action use case interfaces (inbound ports) + service impls (e.g. SaveExerciseUseCase & impl/SaveExerciseService)
infrastructure/  — framework glue (Spring, JPA, etc.)
  adapters/in/rest/   — @RestController classes
  adapters/out/       — @Repository impls delegating to in-memory DAOs
  configuration/      — @Configuration beans (currently empty)
```

**Conventions**: Inbound use cases are interfaces in `usecases/<entity>/` (e.g. `SaveExerciseUseCase`) with service implementations under `usecases/<entity>/impl/` (e.g. `SaveExerciseService`). Controllers inject use case interfaces. Domain layer must not import Spring/DB annotations.

## Quirks & gotchas

- **Test packages** use `package unitary.com.david13penalver...` (not the standard `src/test/java/com/...` convention). Because the test package differs from the production package, tests MUST import the classes under test explicitly (no same-package access). Controller/DAO integration tests that live outside `com.david13penalver...` also need `@SpringBootTest(classes = FossTrainingApiApplication.class)` — Spring can't auto-discover `@SpringBootConfiguration` from the `unitary` package.
- **In-memory DAOs** back the repository adapters (`InMemoryExerciseDao`, `InMemorySessionDao` — `@Component`, `LinkedHashMap` + `AtomicInteger`, synchronized methods). They simulate the future PostgreSQL DB; `save()` assigns an ID when null and upserts otherwise. Repository impls (`@Repository`) delegate to the DAO. **Do not mock the concrete DAO classes** with Mockito inline mocks — they cannot be instrumented on JDK 26; use real instances or mock the port interface.
- **Jackson annotations live in the domain** (JSON crosses the HTTP boundary directly, no DTO layer yet): abstract `SessionExercise` uses `@JsonTypeInfo`/`@JsonSubTypes` with discriminator `exerciseType` (resistance/endurance/mobility); immutable value objects (`Weight`, `Rpe`, `Distance`, `Duration`, `Pace`) carry `@JsonCreator`/`@JsonProperty` on constructors, and derived getters (`Duration.getMinutes/getHours`, `*SessionExercise.getTotalSetsCount`/`getWorkingSetsCount`/`getTotalReps`/`getHeaviestWeight`/`getIntervalsCount`) are `@JsonIgnore`d so GET output round-trips through strict POST deserialization.
- **Enums serialize as their `name()`** by default; `@Getter`-generated display fields (`getName`, etc.) make GET output noisy but are ignored on deserialization. Enum endpoints return plain name strings (e.g. `GET /api/equipment` → `["BODYWEIGHT", ...]`).
- **Lombok** is used extensively (`@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@RequiredArgsConstructor`, `@Slf4j`). Needs annotation processor configured in IDE. Lombok version is explicitly overridden to 1.18.46 in `pom.xml` for JDK 26 compatibility.
- **application.properties** only has `spring.application.name`; most config (DB connection, etc.) is absent.
- **No CI, formatter, linter, or typecheck config** exists.
- Mockito inline mocks fail to instrument classes with `synchronized` methods on JDK 26 — test with real objects instead.

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
