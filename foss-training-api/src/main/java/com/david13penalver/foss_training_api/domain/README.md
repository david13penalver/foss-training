# Domain Layer

This layer contains the core business logic and entities of the application. It represents the heart of the Hexagonal Architecture.

## Responsibilities
- **Model**: Contains business entities, value objects, and domain enums. These objects are independent of any framework or external library.
- **Ports**: Defines the outbound contracts (interfaces) for interacting with secondary/driven adapters.
    - **Out (Secondary)**: Interfaces implemented by Driven Adapters (e.g., `ExerciseRepository`).

## Rules
- Code in this layer must not depend on any other layer (Application or Infrastructure).
- It must be pure Java code, free from framework annotations (like Spring or JPA).
