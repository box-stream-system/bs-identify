# bs-identify
Identify Service for Box Stream


### JWT
- VALID_DURATION time
- REFRESHABLE_DURATION time


### Encrypt Password
- Bcrypt


### Logout logic
- Add a field token ID to JWT's payload
- Save that ID of token to DB when user do logged-out
- Validate token's ID whenever access
- Impl
  - in generate JWT function: add more a claim ID.
  - jwrId()
  -

### Refresh token machines
- If current token is about to be expired
- Client will call refresh token to get a new one
- Server:
  - Check the current must valid
  - Check current not logged-out (Not exists in InvalidToken table)
  - Check current not expired
  - Also do logged-out the current token
  - Then generate a new token return to client


### Testing
  - JUnit
  - Mockito: for make deal fake data that come from other layer
  - Isolation: Make our test service can run on any environment not depend on any third party.
    - Ex. Instead of connect to MySQL of main program, ee config for Unit test connect to any others like H2 DB, and not effect to the main DB service (DB server, EC2)
    - make a config for Test to other DB like H2, then can run test cases without impact to main DB.
    - Separate Test case with Third-party (Some Third-party will soon not available)

### Code coverage
- A number display how many percent of our project that covered by Unit test.
- JaCoCo:
    - Generate report:
    ```groovy
    ./gradlew test jacocoTestReport
    ```
    - Config exclude packages and class that don't need to check coverage

### Integration Test with TestContainer for fast release
- Unit Test + Integration Test
- ...

### Format code with Spotless
- IntelliJ: Menu bar: Code -> Reformat Code

- Spotless: a plugin for team members, follow the same rule.
  - Auto format code.
  - Check: 
  ```groovy  
  ./gradlew spotlessCheck
  ./gradlew spotlessApply
  ```

### Sonar Lint and SonarQube
- Scan and Report
- Clean Code
- Config SonarQ in local
  - Add your user to the Docker group to avoid using sudo:
  ```groovy
  sudo usermod -aG docker $USER
  newgrp docker
  newgrp docker
  ```
  
  ```groovy  
  docker pull sonarqube:latest
  nano docker-compose.yml
  newgrp docker
  ```
  
  - http://localhost:9000
  - Import src code to Sonar
    - From Github, Gitlab, ...
    - Manual point from local
    
- Manual install sonarQ
- docker pull sonarqube:latest
- http://localhost:9000
- username: admin
- password: admin
- Setting in project
    ```groovy
    plugins {
        id "org.sonarqube" version "4.3.0.3225"
    }
    
    sonarqube {
        properties {
        property "sonar.projectKey", "BoxStreamIdentity"
        property "sonar.host.url", "http://localhost:9000"
        property "sonar.login", "token"
        }
    }
  ```
- Run in project folder
  ```groovy
  ./gradlew sonarqube
  ```
- Check detail at http://localhost:9000


### Concurrent - Unique field in JPA
- If hacker create 10 requests at the same time with the same User?
- What happened?
- Install JMeter to your PC
- Use JMeter to fake concurrent request to APIs. 
```groovy
if (userRepository.existsByUsername(newUser.getUsername())) throw new UsernameExistsException();
```

- In case you make a tool insert many user with the same user at the same time.
- There will be many user insert with the same username in Database.
- How to solve? 
- Make column "username" UNIQUE
```groovy
@Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username; //utf8mb4_unicode_ci not specific uppercase and lowercase
```
- Done.