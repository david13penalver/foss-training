# Clean Code Tips for Maintainable and Scalable Projects

## Table of Contents
- [Code Organization](#code-organization)
- [Naming Conventions](#naming-conventions)
- [Function Design](#function-design)
- [Error Handling](#error-handling)
- [Testing](#testing)
- [Documentation](#documentation)
- [Performance](#performance)
- [Security](#security)
- [Git Best Practices](#git-best-practices)

## Code Organization

### Single Responsibility Principle
- Each function/class should have one reason to change
- Keep functions focused on a single task
- Avoid god classes and functions that do too much

### Directory Structure (Hexagonal Architecture)
```
src/main/java/com/yourcompany/yourapp/
├── domain/                 # Core business logic
│   ├── model/             # Domain entities and value objects
│   │   ├── exercise/
│   │   ├── session/
│   │   └── user/
│   └── ports/             # Domain interfaces
│       ├── in/            # Input ports (use case interfaces)
│       └── out/           # Output ports (repository interfaces)
├── application/           # Application services and use cases
│   ├── services/          # Application services
│   └── usecases/          # Business use cases
├── infrastructure/        # External implementations
│   ├── adapters/
│   │   ├── in/           # Driving adapters (REST controllers)
│   │   └── out/          # Driven adapters (JPA repositories)
│   └── configuration/     # Framework configuration
└── YourApplication.java   # Main application class
```

### File Organization
- One main export per file
- Group related functionality together
- Use index files for clean imports

## Naming Conventions

### General Rules
- Use descriptive, meaningful names
- Avoid abbreviations unless widely understood
- Be consistent across the codebase

### Variables and Methods
- Use camelCase for variables and methods
- Name should describe what it does
- Boolean variables should start with `is`, `has`, `can`, `should`

```java
// Good
private boolean isAuthenticated;
private List<User> users;
public UserData getUserData() {}
public boolean hasPermission() {}

// Bad
private boolean auth;
private List<User> u;
public UserData getData() {}
public boolean perm() {}
```

### Classes and Interfaces
- Use PascalCase for classes and interfaces
- Name should describe what it represents
- Interfaces often start with "I" or describe capability

```java
// Good
public class UserService {}
public class UserProfile {}
public interface ExerciseRepository {}
public interface UserRepository {}

// Bad
public class service {}
public class profile {}
public interface Repo {}
```

### Constants
- Use UPPER_SNAKE_CASE for constants
- Group related constants together
- Use static final for constants

```java
// Good
public static final String API_BASE_URL = "https://api.example.com";
public static final int MAX_RETRY_ATTEMPTS = 3;
public static final long DEFAULT_TIMEOUT = 5000L;

// In separate constants class
public class ApiConstants {
    public static final String BASE_URL = "https://api.example.com";
    public static final String VERSION = "v1";
    public static final int TIMEOUT = 30000;
}
```

## Function Design

### Function Length
- Keep functions under 20-30 lines
- If longer, consider breaking into smaller functions
- Each function should do one thing well

### Parameters
- Limit to 3-4 parameters maximum
- Use parameter objects (DTOs) for related parameters
- Provide method overloading or builder pattern for complex objects

```java
// Good
public User createUser(CreateUserRequest request) {
    return User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .role(request.getRole() != null ? request.getRole() : "user")
        .build();
}

// Bad
public User createUser(String name, String email, String role, 
                      String department, String location, String manager) {
    // implementation
}
```

### Pure Methods
- Prefer pure methods when possible
- Avoid side effects in utility methods
- Make methods predictable and testable

```java
// Good
public BigDecimal calculateTotal(List<OrderItem> items) {
    return items.stream()
        .map(OrderItem::getPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
}

// Bad
private BigDecimal total = BigDecimal.ZERO;
public BigDecimal calculateTotal(List<OrderItem> items) {
    items.forEach(item -> {
        total = total.add(item.getPrice());
    });
    return total;
}
```

## Error Handling

### Consistent Error Handling
- Use try-catch blocks for operations that may throw exceptions
- Create custom exception classes for specific scenarios
- Log errors with sufficient context

```java
// Good
public class ValidationException extends RuntimeException {
    private final String field;
    
    public ValidationException(String message, String field) {
        super(message);
        this.field = field;
    }
    
    public String getField() {
        return field;
    }
}

public void validateUser(User user) {
    if (user.getEmail() == null || user.getEmail().isEmpty()) {
        throw new ValidationException("Email is required", "email");
    }
}
```

### Graceful Degradation
- Provide fallbacks when possible
- Don't let errors crash the entire application
- Show user-friendly error messages

## Testing

### Test Structure
- Arrange, Act, Assert pattern
- Test both success and failure cases
- Use descriptive test names

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    @DisplayName("Should create user with valid data")
    void shouldCreateUserWithValidData() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("John", "john@example.com");
        User expectedUser = new User("John", "john@example.com");
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);
        
        // Act
        User actualUser = userService.createUser(request);
        
        // Assert
        assertThat(actualUser.getName()).isEqualTo("John");
        assertThat(actualUser.getEmail()).isEqualTo("john@example.com");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    @DisplayName("Should throw exception when email is null")
    void shouldThrowExceptionWhenEmailIsNull() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest("John", null);
        
        // Act & Assert
        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Email is required");
    }
}
```

### Test Coverage
- Aim for 80%+ code coverage
- Focus on critical business logic
- Test edge cases and error conditions

### Test Types
- Unit tests for individual functions
- Integration tests for component interactions
- End-to-end tests for user workflows

## Documentation

### Code Comments
- Comment why, not what
- Document complex algorithms
- Use TODO/FIXME comments sparingly

```java
// Good
/**
 * Using exponential backoff to prevent overwhelming the server
 * and handle temporary network failures gracefully.
 */
public CompletableFuture<Result> retryWithBackoff(Supplier<CompletableFuture<Result>> operation, int maxRetries) {
    // implementation
}

// Bad
/**
 * This method retries an operation
 */
public CompletableFuture<Result> retryWithBackoff(Supplier<CompletableFuture<Result>> operation, int maxRetries) {
    // implementation
}
```

### README Files
- Project overview and purpose
- Installation and setup instructions
- Usage examples
- Contributing guidelines

### API Documentation
- Document all public APIs
- Include parameter types and return values
- Provide usage examples

## Performance

### Optimization Principles
- Measure before optimizing
- Focus on bottlenecks
- Consider readability over micro-optimizations

### Memory Management
- Avoid memory leaks
- Clean up resources properly
- Use appropriate data structures
- Implement AutoCloseable for resources

```java
// Good
public class EventManager implements AutoCloseable {
    private final Map<String, Set<EventListener>> listeners = new ConcurrentHashMap<>();
    
    public void addListener(String event, EventListener callback) {
        listeners.computeIfAbsent(event, k -> ConcurrentHashMap.newKeySet())
                .add(callback);
    }
    
    public void removeListener(String event, EventListener callback) {
        listeners.get(event)?.remove(callback);
    }
    
    @Override
    public void close() {
        listeners.clear();
    }
}

// Resource management with try-with-resources
public void processEvents() {
    try (EventManager eventManager = new EventManager()) {
        // Use eventManager
        // Automatically closed when exiting try block
    }
}
```

### Async Operations
- Use CompletableFuture or reactive programming
- Implement proper error handling
- Consider timeouts and circuit breakers

```java
// Good
public CompletableFuture<User> getUserAsync(Long userId) {
    return CompletableFuture.supplyAsync(() -> userRepository.findById(userId))
            .orTimeout(5, TimeUnit.SECONDS)
            .exceptionally(throwable -> {
                log.error("Failed to get user: {}", userId, throwable);
                throw new UserNotFoundException("User not found: " + userId);
            });
}

// Using Spring's @Async
@Async
public CompletableFuture<List<Exercise>> getExercisesByCategory(ExerciseCategory category) {
    List<Exercise> exercises = exerciseRepository.findByCategory(category);
    return CompletableFuture.completedFuture(exercises);
}
```

## Security

### Input Validation
- Validate all user inputs
- Use Bean Validation annotations
- Sanitize data before processing
- Use parameterized queries for database operations

```java
// Good
public class CreateUserRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    // Getters and setters
}

@Service
public class UserService {
    
    public User createUser(CreateUserRequest request) {
        // Bean validation automatically applied by Spring
        return userRepository.save(User.fromRequest(request));
    }
}

// Repository with parameterized queries
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
    
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findById(@Param("userId") Long userId);
}
```

### Authentication and Authorization
- Implement proper authentication
- Use principle of least privilege
- Secure sensitive data with encryption

### Dependency Security
- Keep dependencies updated
- Use security scanning tools
- Review third-party packages

## Git Best Practices

### Commit Messages
- Use conventional commit format
- Write descriptive messages
- Keep commits focused and atomic

```
feat: add user authentication
fix: resolve login validation issue
docs: update API documentation
refactor: simplify user service logic
```

### Branch Strategy
- Use feature branches for new development
- Keep main branch stable
- Use pull requests for code review

### Code Review
- Review for functionality and style
- Check for potential security issues
- Ensure tests are included

## Additional Tips

### Code Quality Tools
- Use static analysis tools (SpotBugs, PMD, SonarQube)
- Set up pre-commit hooks with checkstyle
- Configure CI/CD pipelines with Maven/Gradle
- Use JaCoCo for code coverage

```xml
<!-- Maven plugins for code quality -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>

<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.7.3.0</version>
</plugin>
```

### Refactoring
- Refactor regularly, not just at the end
- Keep tests passing during refactoring
- Make small, incremental changes

### Monitoring and Logging
- Implement proper logging
- Monitor application performance
- Set up alerts for critical issues

### Configuration Management
- Separate configuration from code
- Use Spring Boot's configuration properties
- Provide default configurations
- Externalize sensitive data

```java
// Good
@ConfigurationProperties(prefix = "app")
@Data
public class ApplicationProperties {
    
    private String name;
    private String version;
    private final Api api = new Api();
    private final Database database = new Database();
    
    @Data
    public static class Api {
        private String baseUrl;
        private int timeout = 30000;
        private int maxRetries = 3;
    }
    
    @Data
    public static class Database {
        private String url;
        private int maxConnections = 10;
    }
}

// application.yml
app:
  name: FOSS Training API
  version: 1.0.0
  api:
    base-url: https://api.example.com
    timeout: 30000
  database:
    url: ${DATABASE_URL:jdbc:h2:mem:testdb}
    max-connections: 20
```

---

Remember: Clean code is not about following rules blindly, but about writing code that is easy to understand, maintain, and extend. The best code is the code that future developers (including yourself) can easily work with.