package com.quyhoang.flexistudy.service.impl;

import com.quyhoang.flexistudy.dto.AdminStatsResponse;
import com.quyhoang.flexistudy.dto.PeriodRevenueDTO;
import com.quyhoang.flexistudy.enums.RoleName;
import com.quyhoang.flexistudy.repository.PaymentRepository;
import com.quyhoang.flexistudy.repository.UserRepository;
import com.quyhoang.flexistudy.service.AdminStatsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminStatsServiceImpl implements AdminStatsService {

    UserRepository userRepository;
    PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public AdminStatsResponse getAdminSummary(String period, Integer value, Integer year) {

        LocalDateTime startDate;
        LocalDateTime endDate;

        if ("month".equalsIgnoreCase(period) && value != null && year != null) {
            LocalDate start = LocalDate.of(year, value, 1);
            startDate = start.atStartOfDay();
            endDate = start.plusMonths(1).atStartOfDay();
        } else if ("quarter".equalsIgnoreCase(period) && value != null && year != null) {
            int startMonth = (value - 1) * 3 + 1;
            LocalDate start = LocalDate.of(year, startMonth, 1);
            startDate = start.atStartOfDay();
            endDate = start.plusMonths(3).atStartOfDay();
        } else if ("year".equalsIgnoreCase(period) && year != null) {
            LocalDate start = Year.of(year).atDay(1);
            startDate = start.atStartOfDay();
            endDate = start.plusYears(1).atStartOfDay();
        } else {
            startDate = LocalDate.of(2000, 1, 1).atStartOfDay();
            endDate = LocalDate.now().plusDays(1).atStartOfDay();
        }

        String SUCCESS = "success";

        Long totalRevenue = paymentRepository.sumAmountByStatusAndCreatedAtBetween(SUCCESS, startDate, endDate);
        long totalTransactions = paymentRepository.countByStatusAndCreatedAtBetween(SUCCESS, startDate, endDate);
        long totalUsers = userRepository.count();
        long totalCandidates = userRepository.countByRoleName(RoleName.USER);
        long totalEmployers = userRepository.countByRoleName(RoleName.RECRUITER);

        // Chọn query phù hợp
        List<Object[]> rawData;
        if ("month".equalsIgnoreCase(period)) {
            rawData = paymentRepository.getDailyRevenueNative(SUCCESS, startDate, endDate); // theo ngày
        } else {
            rawData = paymentRepository.getMonthlyRevenueNative(SUCCESS, startDate, endDate); // theo tháng
        }

        List<PeriodRevenueDTO> chartData = rawData.stream()
                .map(r -> new PeriodRevenueDTO((String) r[0], ((Number) r[1]).longValue()))
                .toList();

        return AdminStatsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalTransactions(totalTransactions)
                .totalUsers(totalUsers)
                .totalCandidates(totalCandidates)
                .totalEmployers(totalEmployers)
                .periodicRevenue(chartData)
                .build();
    }

}
