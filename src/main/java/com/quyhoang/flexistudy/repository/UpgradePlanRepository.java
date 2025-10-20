package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.UpgradePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UpgradePlanRepository extends JpaRepository<UpgradePlan, Long> {
    Optional<UpgradePlan> findByNameIgnoreCase(String name);

}
