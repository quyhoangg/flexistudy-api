package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.request.PayOSCallbackRequest;
import com.quyhoang.flexistudy.entity.Payment;
import com.quyhoang.flexistudy.entity.UpgradePlan;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.repository.PaymentRepository;
import com.quyhoang.flexistudy.repository.UpgradePlanRepository;
import com.quyhoang.flexistudy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;
    private final UpgradePlanRepository upgradePlanRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void createSuccessPayment(PayOSCallbackRequest payload) {

        User user = userRepository.findById(payload.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));


        UpgradePlan plan = upgradePlanRepository.findById(payload.getPlanId())
                .orElseThrow(() -> new RuntimeException("Upgrade plan not found"));


        user.setUpgradePlan(plan);

        LocalDate today = LocalDate.now();
        LocalDate currentExpiration = user.getPlanExpirationDate();
        LocalDate newExpiration = (currentExpiration != null && currentExpiration.isAfter(today))
                ? currentExpiration.plusDays(plan.getDurationInDays())
                : today.plusDays(plan.getDurationInDays());
        user.setPlanExpirationDate(newExpiration);

        userRepository.save(user);


        Payment payment = Payment.builder()
                .user(user)
                .upgradePlan(plan)
                .orderCode(payload.getOrderCode())
                .transactionId(payload.getTransactionId())
                .amount(payload.getAmount())
                .status(payload.getStatus())
                .build();
        paymentRepository.save(payment);
    }
}
