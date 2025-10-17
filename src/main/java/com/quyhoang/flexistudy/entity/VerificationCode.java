package com.quyhoang.flexistudy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String email;
    String code;
    LocalDateTime expiresAt;
}
