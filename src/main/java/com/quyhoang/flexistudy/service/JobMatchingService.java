package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.JobMatchResult;
import com.quyhoang.flexistudy.entity.Job;
import com.quyhoang.flexistudy.entity.JobShift;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.enums.JobStatus;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.repository.JobRepository;
import com.quyhoang.flexistudy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobMatchingService {
    UserRepository userRepository;
    JobRepository jobRepository;

    public List<JobMatchResult> findMatchingJobs(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Job> openJobs = jobRepository.findAll().stream()
                .filter(j -> j.getStatus() == JobStatus.OPEN)
                .toList();

        return openJobs.stream()
                .map(job -> {
                    double skillScore = calculateSkillScore(user, job);
                    boolean timeOk = isTimeCompatible(user, job);
                    return new JobMatchResult(job, skillScore, timeOk);
                })
                .filter(r -> r.getSkillScore() > 0 && r.isTimeCompatible())
                .sorted(Comparator.comparing(JobMatchResult::getSkillScore).reversed())
                .toList();
    }

    private double calculateSkillScore(User user, Job job) {
        Set<String> userSkills = user.getSkills().stream()
                .map(s -> s.getName().toLowerCase())
                .collect(Collectors.toSet());

        Set<String> jobSkills = job.getRequiredSkills().stream()
                .map(s -> s.getName().toLowerCase())
                .collect(Collectors.toSet());

        if (jobSkills.isEmpty()) return 0;
        long matched = jobSkills.stream().filter(userSkills::contains).count();
        return (double) matched / jobSkills.size();
    }

    private boolean isTimeCompatible(User user, Job job) {
        for (JobShift shift : job.getJobShifts()) {
            boolean available = user.getAvailabilityWindows().stream().anyMatch(a ->
                    a.getDayOfWeek() == shift.getDayOfWeek()
                            && !shift.getStartTime().isBefore(a.getStartTime())
                            && !shift.getEndTime().isAfter(a.getEndTime())
            );
            if (!available) return false;
        }
        return true;
    }
}
