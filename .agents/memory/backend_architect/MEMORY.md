# Backend Architect Memory — FOSS Training

Index of backend design patterns, JPA mapping solutions, Liquibase migrations, and JDK 26 quirks.

## Initial Knowledge
- **Environment**: Java 26 via `mise` (`~/.local/share/mise/installs/java/26.0.2`), Spring Boot 4.1.x/3.x, PostgreSQL 17.
- **Test Package**: Test classes live under `unitary.com.david13penalver.foss_training_api...`.
- **JDK 26 Mockito**: Mockito inline mocks cannot instrument classes with `synchronized` methods on JDK 26.
- **Lombok Override**: Lombok pinned to `1.18.46` in `pom.xml` for javac on JDK 26 compatibility.
- **Liquibase**: `db.changelog-master.yaml` auto-executes against in-memory H2 in PostgreSQL compatibility mode during tests.
- **Enum Deserialization**: Case-insensitive matching via `@JsonCreator fromString(String value)`, returning `null` when input is `null`.

---
<!-- Add entries with format:
- [name](file.md) — brief description
-->
