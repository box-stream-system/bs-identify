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
        ./gradlew test jacocoTestReport
    - Config exclude packages and class that don't need to check coverage

### Integration Test with TestContainer for fast release
- Unit Test + Integration Test
- 
