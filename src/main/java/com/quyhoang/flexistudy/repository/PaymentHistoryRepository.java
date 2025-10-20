package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    Optional<PaymentHistory> findByOrderCode(String orderCode);
}

