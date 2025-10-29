package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.AdminStatsResponse;
import com.quyhoang.flexistudy.dto.ApiResponse;
import com.quyhoang.flexistudy.service.AdminStatsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminStatsController {
    AdminStatsService adminStatsService;

    @GetMapping
    public ApiResponse<AdminStatsResponse> getAdminSummary(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) Integer value,
            @RequestParam(required = false) Integer year
    ) {
        return ApiResponse.<AdminStatsResponse>builder()
                .result(adminStatsService.getAdminSummary(period, value, year))
                .build();
    }
}
