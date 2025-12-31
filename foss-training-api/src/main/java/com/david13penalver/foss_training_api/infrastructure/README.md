# Infrastructure Layer

This layer contains the implementation of the adapters that allow the application to interact with the outside world.

## Responsibilities
- **Adapters**:
    - **In (Driving)**: Adapters that trigger the application (e.g., REST Controllers, CLI commands). They call the Input Ports.
    - **Out (Driven)**: Adapters that are triggered by the application (e.g., JPA Repositories, External API Clients). They implement the Output Ports.
- **Configuration**: Framework configuration classes (e.g., Spring Boot configuration beans).

## Rules
- This layer depends on the Domain and Application layers.
- It contains all the framework-specific code (e.g., Spring annotations, Database drivers).
- The rest of the application should remain ignorant of the details implemented here.
