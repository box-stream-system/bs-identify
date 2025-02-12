package com.boxstream.bs_identity.dto.response;

import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
  String id;

  String username;

  String firstName;

  String lastName;

  String middleName;

  String email;

  String phone;

  String dateOfBirth;

  Set<RoleResponse> roles;
}
