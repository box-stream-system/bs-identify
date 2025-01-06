package com.boxstream.bs_identity.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @Size(min = 8, max = 30, message = "Username must be at least 8 characters")
     String username;

     String password;

     String firstName;

     String lastName;

     String middleName;

     String email;

     String phone;

     String dateOfBirth;
}
