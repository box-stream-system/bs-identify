package com.boxstream.bs_identity.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

     String password;

     String firstName;

     String lastName;

     String middleName;

     String email;

     String phone;

     String dateOfBirth;

     Set<String> roles;
}
