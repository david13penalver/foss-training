---
name: tdd-workflow
description: Use this skill when writing new features, fixing bugs, or refactoring code. Enforces test-driven development (Red-Green-Refactor) across Java 26 (Spring Boot), Angular 22 (Vitest), and Swift 6 (Swift Testing).
---

# Test-Driven Development (TDD) Workflow for FOSS Training

This skill enforces strict Test-Driven Development across all three components of the FOSS Training monorepo:
1. **Backend (`foss-training-api`)**: Java 26, Spring Boot, JUnit 5, Mockito, AssertJ
2. **Frontend (`foss-training-web`)**: Angular 22, TypeScript, Vitest, JSDOM
3. **Mobile (`foss-training-ios`)**: Swift 6, SwiftUI, SwiftData, Swift Testing, XCTest

---

## 1. The Red → Green → Refactor Cycle

1. **RED:** Write a failing test defining the expected behavior, contract, or edge case. Run it to verify it fails as expected.
2. **GREEN:** Write the minimal implementation required to make the test pass.
3. **REFACTOR:** Clean up the design, remove duplication, and improve abstractions while keeping all tests green.

---

## 2. Platform A: Backend TDD (Java 26 + Spring Boot)

### Commands
```bash
# Single test class
JAVA_HOME=$(mise where java) ./mvnw test -Dtest=<TestClassName>

# Specific test method
JAVA_HOME=$(mise where java) ./mvnw test -Dtest=<TestClassName>#<methodName>

# Full backend test suite
JAVA_HOME=$(mise where java) ./mvnw clean test
```

### Invariants & Patterns
- **Test Location & Package**: Backend tests reside under `src/test/java/unitary/com/david13penalver/foss_training_api/...`.
- **Pure Domain Unit Tests**: For domain calculations (e.g., `OneRepMaxFormulaTest`, `ExerciseTest`), instantiate domain objects directly without Spring context.
  ```java
  @Test
  void shouldCalculateEpleyOneRepMaxAccurately() {
      double result = OneRepMaxFormula.EPLEY.calculate(100.0, 5);
      assertEquals(116.67, result, 0.01);
  }
  ```
- **Application Use Case Tests**: Mock outbound ports (e.g. `ExerciseRepository`) with Mockito. Note: On JDK 26, mock interfaces or use real objects rather than mocking classes with synchronized methods.
- **Integration / Controller Tests**: Use `@SpringBootTest(classes = FossTrainingApiApplication.class)` and `@AutoConfigureMockMvc`. Tests execute against in-memory H2 in PostgreSQL mode with Liquibase changelogs automatically applied.

---

## 3. Platform B: Frontend TDD (Angular 22 + Vitest)

### Commands
```bash
cd foss-training-web

# Single non-interactive run
npm test -- --watch=false

# Watch mode during active development
npm test
```

### Invariants & Patterns
- **Signal-Aware Testing**: Test signal state and reactive computations:
  ```typescript
  it('should compute total volume reactively when sets are added', () => {
    const fixture = TestBed.createComponent(WorkoutDashboardComponent);
    const component = fixture.componentInstance;
    component.logSet({ reps: 10, weightKg: 80 });
    expect(component.totalVolumeKg()).toBe(800);
  });
  ```
- **Service & HTTP Mocking**: Use `provideHttpClientTesting()` and `HttpTestingController` to verify request paths, HTTP methods, and response payloads.
- **Standalone Components**: Provide all child component dependencies in `imports: [MyComponent, ...]` within `TestBed.configureTestingModule`.

---

## 4. Platform C: Mobile iOS TDD (Swift 6 + Swift Testing)

### Commands (macOS / Xcode)
```bash
xcodebuild -project FOSSTraining.xcodeproj -scheme FOSSTraining -destination 'generic/platform=iOS Simulator' test
```

### Invariants & Patterns
- **Swift Testing Framework**: Use modern `@Suite` and `@Test` macros:
  ```swift
  import Testing
  @testable import FOSSTraining

  @Suite("One Rep Max Calculation Tests")
  struct OneRepMaxTests {
      @Test("Epley formula returns exact match")
      func testEpley() {
          let estimate = OneRepMaxFormula.epley.calculate(weightKg: 100.0, repetitions: 5)
          #expect(estimate == 116.67)
      }
  }
  ```
- **Hermetic In-Memory SwiftData**: SwiftData repository tests MUST use in-memory `ModelContainer` (`isStoredInMemoryOnly: true`) to avoid disk writes and test pollution:
  ```swift
  let config = ModelConfiguration(isStoredInMemoryOnly: true)
  let container = try ModelContainer(for: SDExercise.self, configurations: config)
  let context = ModelContext(container)
  ```
- **Strict Concurrency**: Models and mock repositories passed across actor boundaries must conform to `Sendable`. ViewModels must run on `@MainActor`.

---

## 5. Anti-Patterns to Avoid

| Anti-Pattern | Why It Fails | Correct Approach |
|---|---|---|
| Skipping `JAVA_HOME=$(mise where java)` | Build may run against default JDK 21 instead of JDK 26 | Always prefix Maven commands with `JAVA_HOME=$(mise where java)` |
| Writing to disk in SwiftData tests | Causes test pollution, locks, and simulator file leaks | Always use `ModelConfiguration(isStoredInMemoryOnly: true)` |
| Testing private Angular state directly | Fragile tests that break upon refactoring | Test public signals, outputs, and DOM rendering |
| Modifying domain models to suit wire formats | Violates Hexagonal architecture separation | Keep domain pure; use DTOs and mappers in infrastructure |
| Ignoring sports science rounding discrepancies | Creates mismatch between web, mobile, and backend | Round all 1RM/formula calculations to 2 decimal places |
