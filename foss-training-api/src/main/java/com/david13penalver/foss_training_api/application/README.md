# Application Layer

This layer acts as the glue between the Domain layer and the Infrastructure layer. It contains the application's use cases and services.

## Responsibilities
- **Use Cases**: Specific business actions that coordinate domain objects to perform a task. They implement the "Input Ports" defined in the Domain layer.
- **Services**: Application services that orchestrate calls to the domain and infrastructure (via output ports).

## Rules
- This layer depends on the Domain layer.
- It does **not** depend on the Infrastructure layer (it uses the interfaces/ports defined in Domain).
- It handles the flow of data but delegates business rules to the Domain entities.
