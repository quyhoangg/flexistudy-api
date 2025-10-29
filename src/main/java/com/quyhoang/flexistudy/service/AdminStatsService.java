package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.AdminStatsResponse;

public interface AdminStatsService {
    AdminStatsResponse getAdminSummary(String period, Integer value, Integer year);
}
