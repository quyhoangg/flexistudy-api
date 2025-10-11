package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.request.ApplicationCreateRequest;
import com.quyhoang.flexistudy.dto.request.ApplicationUpdateRequest;
import com.quyhoang.flexistudy.entity.Application;
import com.quyhoang.flexistudy.entity.Job;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.enums.ApplicationStatus;
import com.quyhoang.flexistudy.enums.JobStatus;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.repository.ApplicationRepository;
import com.quyhoang.flexistudy.repository.JobRepository;
import com.quyhoang.flexistudy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationService {
    ApplicationRepository applicationRepository;
    JobRepository jobRepository;
    UserRepository userRepository;

    @Transactional
    public Application create(ApplicationCreateRequest req) {
        if (applicationRepository.existsByUser_IdAndJob_Id(req.getUserId(), req.getJobId())) {
            throw new AppException(ErrorCode.APPLIED_ALREADY);
        }

        Job job = jobRepository.findById(req.getJobId())
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));;

        if (job.getStatus() == JobStatus.CLOSED) {
            throw new AppException(ErrorCode.JOB_CLOSED);
        }

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));;

        Application app = Application.builder()
                .job(job)
                .user(user)
                .status(ApplicationStatus.PENDING)
                .note(req.getNote())
                .build();

        return applicationRepository.save(app);
    }

    @Transactional
    public Application updateStatus(String id, ApplicationUpdateRequest req) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        app.setStatus(req.getStatus());
        if (req.getNote() != null) {
            app.setNote(req.getNote());
        }
        return applicationRepository.save(app);
    }

    public List<Application> getAllByUser(String userId) {
        return applicationRepository.findByUser_Id(userId);
    }

    public List<Application> getAllByJob(String jobId) {
        return applicationRepository.findByJob_Id(jobId);
    }

    public Application getById(String id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));
    }

    @Transactional
    public void delete(String id) {
        if (!applicationRepository.existsById(id))
            throw new AppException(ErrorCode.APPLICATION_NOT_FOUND);
        applicationRepository.deleteById(id);
    }
}
