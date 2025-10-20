package com.quyhoang.flexistudy.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpgradePlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String name; // Tên gói (VD: Basic, Premium, Pro)

    @Column(nullable = false)
    double price; // Giá gói

    @Column(nullable = false)
    int durationInDays; // Số ngày hiệu lực

    String description; // Mô tả lợi ích của gói
}
