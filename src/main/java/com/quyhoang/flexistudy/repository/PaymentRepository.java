package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.dto.PeriodRevenueDTO;
import com.quyhoang.flexistudy.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.status = :status
          AND p.createdAt BETWEEN :start AND :end
    """)
    Long sumAmountByStatusAndCreatedAtBetween(
            @Param("status") String status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT COUNT(p)
        FROM Payment p
        WHERE p.status = :status
          AND p.createdAt BETWEEN :start AND :end
    """)
    long countByStatusAndCreatedAtBetween(
            @Param("status") String status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 🟢 Native query fix cho MySQL + ONLY_FULL_GROUP_BY
    @Query(
            value = """
            SELECT DATE_FORMAT(p.created_at, '%Y-%m') AS period,
                   COALESCE(SUM(p.amount), 0) AS revenue
            FROM payment p
            WHERE p.status = :status
              AND p.created_at BETWEEN :start AND :end
            GROUP BY period
            ORDER BY period
        """,
            nativeQuery = true
    )
    List<Object[]> getMonthlyRevenueNative(
            @Param("status") String status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(
            value = """
            SELECT DATE_FORMAT(p.created_at, '%Y-%m-%d') AS period,
                   COALESCE(SUM(p.amount), 0) AS revenue
            FROM payment p
            WHERE p.status = :status
              AND p.created_at BETWEEN :start AND :end
            GROUP BY period
            ORDER BY period
        """,
            nativeQuery = true
    )
    List<Object[]> getDailyRevenueNative(
            @Param("status") String status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
