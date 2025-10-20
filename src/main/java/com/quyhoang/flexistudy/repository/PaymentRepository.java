package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
