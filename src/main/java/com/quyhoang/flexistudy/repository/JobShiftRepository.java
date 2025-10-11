package com.quyhoang.flexistudy.repository;

import com.quyhoang.flexistudy.entity.JobShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobShiftRepository extends JpaRepository<JobShift, String> {
    List<JobShift> findByJob_Id(String jobId);
}
