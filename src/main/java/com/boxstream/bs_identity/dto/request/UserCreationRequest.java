package com.boxstream.bs_identity.dto.request;

import com.boxstream.bs_identity.validator.DobConstraint;
import com.boxstream.bs_identity.validator.PasswordConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @Size(min = 8, max = 30, message = "USERNAME_INVALID")
    String username;

    @PasswordConstraint(min = 8)
    String password;

    String firstName;

    String lastName;

    String middleName;

    String email;

    String phone;

    @DobConstraint(min = 15)
    LocalDate dateOfBirth;
}
