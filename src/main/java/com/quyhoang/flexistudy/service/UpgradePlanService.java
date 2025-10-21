package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.entity.UpgradePlan;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.repository.UpgradePlanRepository;
import com.quyhoang.flexistudy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpgradePlanService {

    private final UpgradePlanRepository upgradePlanRepository;
    private final UserRepository userRepository;

    public List<UpgradePlan> getAllPlans() {
        return upgradePlanRepository.findAll();
    }

    public Optional<UpgradePlan> getPlanById(Long id) {
        return upgradePlanRepository.findById(id);
    }

    public UpgradePlan createPlan(UpgradePlan plan) {
        return upgradePlanRepository.save(plan);
    }

    public UpgradePlan updatePlan(Long id, UpgradePlan plan) {
        return upgradePlanRepository.findById(id).map(existing -> {
            existing.setName(plan.getName());
            existing.setPrice(plan.getPrice());
            existing.setDurationInDays(plan.getDurationInDays());
            existing.setDescription(plan.getDescription());
            return upgradePlanRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Plan not found with id " + id));
    }

    public void deletePlan(Long id) {
        upgradePlanRepository.deleteById(id);
    }

    public User upgradeUserPlan(String userId, Long planId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        UpgradePlan plan = upgradePlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found with id " + planId));

        user.setUpgradePlan(plan);
        user.setPlanExpirationDate(LocalDate.now().plusDays(plan.getDurationInDays()));

        return userRepository.save(user);
    }
}
