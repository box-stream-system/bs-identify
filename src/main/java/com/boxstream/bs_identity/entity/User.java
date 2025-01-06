package com.boxstream.bs_identity.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
