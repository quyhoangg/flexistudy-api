package com.quyhoang.flexistudy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCv {
    @Id
    @UuidGenerator
    String id;

    @Column(nullable = false)
    String userId;

    @Column(nullable = false)
    String fileName;

    @Column(nullable = false)
    String fileUrl;

    @Column(nullable = false)
    String fileType;

    @Column(nullable = false)
    long fileSize;

    @Column(nullable = false)
    LocalDateTime uploadedAt;

    boolean isPrimary;
}
