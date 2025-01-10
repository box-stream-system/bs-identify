# bs-identify
Identity Service for Box Stream

## Features

### JWT (JSON Web Token)
- **VALID_DURATION:** Specifies the token's validity period.
- **REFRESHABLE_DURATION:** Specifies the period during which the token can be refreshed.

### Password Encryption
- Uses **Bcrypt** for secure password hashing.

### Logout Logic
- Add a `token ID` field to the JWT payload.
- Save the token's ID to the database when a user logs out.
- Validate the token's ID during every request.
- Implementation:
  - In the JWT generation function, add a custom claim for `ID`.
  - Use `jwrId()` to retrieve the token ID.

### Refresh Token Mechanism
- If the current token is about to expire:
  - **Client:** Calls the refresh token API to obtain a new token.
  - **Server:**
    1. Validates the current token.
    2. Ensures the token has not been logged out (not present in the `InvalidToken` table).
    3. Confirms the token is not expired.
    4. Logs out the current token.
    5. Generates a new token and returns it to the client.

## Testing
- **JUnit:** Used for writing test cases.
- **Mockito:** Simulates data from other layers for isolated testing.
- **Environment Isolation:** Ensure tests are independent of third-party systems.
  - Example: Instead of connecting to the production MySQL database, configure tests to use an in-memory database like **H2**.
  - This prevents test cases from affecting the main database.
- Separate test cases from third-party integrations to maintain stability.

## Code Coverage
- Displays the percentage of project code covered by unit tests.
- **JaCoCo:** A plugin for generating coverage reports.
  - Generate the report using:
    ```bash
    ./gradlew test jacocoTestReport
    ```
  - Configure exclusions for specific packages or classes that don't require coverage.

## Integration Testing with TestContainers
- Combine **Unit Tests** and **Integration Tests** for reliable, fast releases.

## Code Formatting with Spotless
- IntelliJ:
  - Navigate to: **Code** > **Reformat Code**.
- **Spotless Plugin:** Ensures consistent code style among team members.
  - Auto-format code using:
    ```bash
    ./gradlew spotlessApply
    ```
  - Verify code formatting:
    ```bash
    ./gradlew spotlessCheck
    ```

## Code Quality with SonarQube
- Scan code for quality, maintainability, and security issues.
- **Local Configuration:**
  1. Add your user to the Docker group to avoid using `sudo`:
     ```bash
     sudo usermod -aG docker $USER
     newgrp docker
     ```
  2. Pull the SonarQube image and set up:
     ```bash
     docker pull sonarqube:latest
     ```
  3. Access SonarQube at [http://localhost:9000](http://localhost:9000).
    - Default credentials:
      - Username: `admin`
      - Password: `admin`
  4. Add the following to your project:
     ```groovy
     plugins {
         id "org.sonarqube" version "4.3.0.3225"
     }

     sonarqube {
         properties {
             property "sonar.projectKey", "BoxStreamIdentity"
             property "sonar.host.url", "http://localhost:9000"
             property "sonar.login", "your-sonar-token"
         }
     }
     ```
  5. Run:
     ```bash
     ./gradlew sonarqube
     ```
  6. View detailed reports at [http://localhost:9000](http://localhost:9000).

## Handling Concurrent Requests in JPA
- Prevent duplicate entries when multiple requests with the same data are submitted simultaneously.
- Example issue:
  - Multiple requests to create a user with the same username may bypass the initial existence check.
- Solution:
  - Make the `username` column unique:
    ```java
    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    private String username;
    ```
  - This ensures case-insensitive uniqueness for usernames.

- Use **JMeter** to simulate concurrent API requests and validate the fix.

## Profiles
- Generate new signing keys for each profile:
  [https://generate-random.org/](https://generate-random.org/)
- **Setting Active Profiles:**
  - Create a profile-specific configuration file (e.g., `application-prod.yaml`).
  - In IntelliJ:
    1. Go to **Run/Debug Configuration**.
    2. Add the following to **VM Options**:
       ```bash
       -Dspring.profiles.active=prod
       ```
  - Sensitive data such as signing keys and database credentials should not be stored in local files.
    - Use environment variables (ENV) to manage sensitive configurations securely.
    - Configure `application-prod.yaml` to read values from the server's environment variables.
    - Example:
      ```yaml
      jwt:
        signerKey: ${JWT_SIGNERKEY}
      ```

- **DevOps Integration:**
  - Store sensitive keys as environment variables on the deployment server.
  - Developers will not have access to production secrets.

---
This improved version enhances readability and provides more detailed instructions for setup and usage. Let me know if there's anything you'd like to adjust or expand further.
