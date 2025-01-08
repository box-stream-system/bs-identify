package com.boxstream.bs_identity.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
/**
 * This Entity is to stored JWT that user logged-out
 * Also stored the expired time of that token: to check and removed all outdated token
 * To make this table will be clean and small
 */
public class InvalidatedToken {
    @Id
    String id;
    Date expiryTime;
}