package com.boxstream.bs_identity.dto.response;

import lombok.Data;

@Data
public class UserUpdateResponse {
    private String id;

    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String middleName;

    private String email;

    private String phone;

    private String dateOfBirth;
}
