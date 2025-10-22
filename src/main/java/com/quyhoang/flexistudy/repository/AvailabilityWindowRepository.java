package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.AvailabilityWindow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AvailabilityWindowRepository extends JpaRepository<AvailabilityWindow,String> {
    List<AvailabilityWindow> findByUser_Id(String userId);

    boolean existsByUser_IdAndDateAndStartTimeAndEndTime(String userId, LocalDate date, LocalTime startTime, LocalTime endTime);
}
