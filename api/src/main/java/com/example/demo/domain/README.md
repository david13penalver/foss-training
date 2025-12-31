# Domain Layer

This layer contains the core business logic and entities of the application. It represents the heart of the Hexagonal Architecture.

## Responsibilities
- **Model**: Contains the business entities and value objects. These objects should be independent of any framework or external library.
- **Ports**: Defines the interfaces (contracts) for interacting with the application.
    - **In (Primary)**: Interfaces called by the Driving Adapters (e.g., Use Case interfaces). These are the domain interfaces.
    - **Out (Secondary)**: Interfaces implemented by the Driven Adapters (e.g., Repository interfaces, External Service interfaces).

## Rules
- Code in this layer must not depend on any other layer (Application or Infrastructure).
- It should be pure Java code, free from framework annotations (like Spring or JPA) as much as possible.
