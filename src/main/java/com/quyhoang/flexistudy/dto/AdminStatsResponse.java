package com.quyhoang.flexistudy.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminStatsResponse {
    Long totalRevenue;
    Long totalTransactions;
    Long totalUsers;
    Long totalCandidates;
    Long totalEmployers;

    List<PeriodRevenueDTO> periodicRevenue;
}
