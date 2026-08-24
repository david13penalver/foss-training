# Application Layer

This layer contains the application's use cases and their implementation services.

## Responsibilities
- **Use Cases (Inbound Ports)**: Specific business action contracts (e.g., `SaveExerciseUseCase`, `FindExerciseByIdUseCase`) called by driving adapters (REST controllers).
- **Services (Use Case Implementations)**: Spring `@Service` classes located under `impl/` (e.g., `SaveExerciseService`, `FindExerciseByIdService`) that coordinate domain logic and invoke outbound ports (repositories) or domain enums.

## Rules
- This layer depends on the Domain layer.
- It does **not** depend on the Infrastructure layer (it calls outbound port interfaces defined in Domain).
- It handles application orchestration while delegating domain business rules to domain entities and models.
