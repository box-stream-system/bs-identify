# bs-identify
Identify Service for Box Stream

### JWT
- Impl follow Oauth2

### Encrypt Password
- Bcrypt

### Logout logic
- Add a field token ID to JWT's payload
- Save that ID of token to DB when user do logged-out
- Validate token's ID whenever access
- Impl
  - in generate JWT function: add more a claim ID.
  - jwrId()

### Refresh token machines
- If current token is about to expired
- Client will call refresh token to get a new one
- Server:
  - Check the current must valid
  - Check current not logged-out (Not exists in InvalidToken table)
  - Check current not expired
  - Also do logged-out the current token
  - Then generate a new token return to client

### Other want to use our API
- Flow:
  - Client:
    - RequestApi, get a token, stored it in their local storage or anywhere
    - Client request many other request using that token
  - Token expired
    - If our server check token expired, then we will ask client a refresh token
    - Client send the refresh token to server
    - If refresh token match and valid
      - Server return a new access token for client
    - If refresh token is expired
      - Server need client logging