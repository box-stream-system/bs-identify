# bs-identify
Identify Service for Box Stream

### Encrypt Password
- Bcrypt

### Logout logic
- Add a field token ID to JWT's payload
- Save that ID of token to DB when user do logged-out
- Validate token's ID whenever access
- Impl
  - in generate JWT function: add more a claim ID.
  - jwrId()
