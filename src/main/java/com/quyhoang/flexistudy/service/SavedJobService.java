package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.response.JobResponse;
import com.quyhoang.flexistudy.entity.Job;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.mapper.JobMapper;
import com.quyhoang.flexistudy.repository.JobRepository;
import com.quyhoang.flexistudy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SavedJobService {

    UserRepository userRepository;
    JobRepository jobRepository;
    JobMapper jobMapper;

    @Transactional
    public void saveJob(String userId, String jobId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        boolean alreadySaved = user.getSavedJobs().stream()
                .anyMatch(j -> j.getId().equals(jobId));

        if (alreadySaved)
            throw new AppException(ErrorCode.JOB_ALREADY_SAVED);

        user.getSavedJobs().add(job);
        userRepository.save(user);
    }

    public boolean isJobSaved(String userId, String jobId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return user.getSavedJobs().stream().anyMatch(j -> j.getId().equals(jobId));
    }

    @Transactional
    public void unsaveJob(String userId, String jobId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        boolean removed = user.getSavedJobs().remove(job);
        if (!removed)
            throw new AppException(ErrorCode.JOB_NOT_SAVED);

        userRepository.save(user);
    }

    public Set<JobResponse> getSavedJobs(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return user.getSavedJobs().stream()
                .map(jobMapper::toJobResponse)
                .collect(Collectors.toSet());
    }
}
