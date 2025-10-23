package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.request.JobShiftCreateRequest;
import com.quyhoang.flexistudy.dto.request.JobShiftUpdateRequest;
import com.quyhoang.flexistudy.entity.Job;
import com.quyhoang.flexistudy.entity.JobShift;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.repository.JobRepository;
import com.quyhoang.flexistudy.repository.JobShiftRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobShiftService {
    JobShiftRepository jobShiftRepository;
    JobRepository jobRepository;

    @Transactional
    public JobShift create(JobShiftCreateRequest req) {
        Job job = jobRepository.findById(req.getJobId())
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        JobShift shift = JobShift.builder()
                .job(job)
                .date(req.getDate() != null ? req.getDate() : LocalDate.of(1970, 1, 1))
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .description(req.getDescription())
                .build();

        return jobShiftRepository.save(shift);
    }

    @Transactional
    public JobShift update(String id, JobShiftUpdateRequest req) {
        JobShift shift = jobShiftRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHIFT_NOT_FOUND));

        shift.setDate(req.getDate());
        shift.setStartTime(req.getStartTime());
        shift.setEndTime(req.getEndTime());
        shift.setDescription(req.getDescription());

        return jobShiftRepository.save(shift);
    }

    @Transactional
    public void delete(String id) {
        if (!jobShiftRepository.existsById(id))
            throw new AppException(ErrorCode.SHIFT_NOT_FOUND);
        jobShiftRepository.deleteById(id);
    }

    public JobShift getById(String id) {
        return jobShiftRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHIFT_NOT_FOUND));
    }

    public List<JobShift> getByJob(String jobId) {
        return jobShiftRepository.findByJob_Id(jobId);
    }
}
