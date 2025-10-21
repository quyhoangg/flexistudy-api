package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.entity.UpgradePlan;
import com.quyhoang.flexistudy.service.UpgradePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/upgrade")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UpgradePlanController {

    private final UpgradePlanService upgradePlanService;

    @GetMapping
    public ResponseEntity<List<UpgradePlan>> getAllPlans() {
        return ResponseEntity.ok(upgradePlanService.getAllPlans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UpgradePlan> getPlanById(@PathVariable Long id) {
        return upgradePlanService.getPlanById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UpgradePlan> createPlan(@RequestBody UpgradePlan plan) {
        return ResponseEntity.ok(upgradePlanService.createPlan(plan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpgradePlan> updatePlan(@PathVariable Long id, @RequestBody UpgradePlan plan) {
        return ResponseEntity.ok(upgradePlanService.updatePlan(id, plan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        upgradePlanService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}
