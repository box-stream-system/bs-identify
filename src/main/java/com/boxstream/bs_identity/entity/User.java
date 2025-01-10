package com.boxstream.bs_identity.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data

@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Table(name = "user")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String username; //utf8mb4_unicode_ci not specific uppercase and lowercase

    String password;

    String firstName;

    String lastName;

    String middleName;

    String email;

    String phone;

    LocalDate dateOfBirth;

    @ManyToMany
    Set<Role> roles;

}
