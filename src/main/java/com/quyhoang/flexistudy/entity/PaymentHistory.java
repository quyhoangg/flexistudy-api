package com.quyhoang.flexistudy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "upgrade_plan_id")
    private UpgradePlan upgradePlan;

    @Column
    private Long amount;

    @Column(unique = true)
    private String orderCode;

    @Column
    private String status;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();
}
