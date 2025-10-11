package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.JobCategoryCount;
import com.quyhoang.flexistudy.dto.PageResponse;
import com.quyhoang.flexistudy.dto.request.JobCreationRequest;
import com.quyhoang.flexistudy.dto.request.JobUpdateRequest;
import com.quyhoang.flexistudy.dto.response.JobResponse;
import com.quyhoang.flexistudy.entity.Company;
import com.quyhoang.flexistudy.entity.Job;
import com.quyhoang.flexistudy.entity.Skill;
import com.quyhoang.flexistudy.enums.JobStatus;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.mapper.JobMapper;
import com.quyhoang.flexistudy.repository.CompanyRepository;
import com.quyhoang.flexistudy.repository.JobRepository;
import com.quyhoang.flexistudy.repository.SkillRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobService {
    JobRepository jobRepository;
    CompanyRepository companyRepository;
    SkillRepository skillRepository;
    JobMapper jobMapper;

    @Transactional
    public JobResponse createJob(JobCreationRequest req) {
        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

        Job job = jobMapper.toJob(req);
        job.setCompany(company);
        job.setStatus(JobStatus.CLOSED);

        //  Nếu chưa có expiryDate → mặc định 30 ngày kể từ hôm nay
        if (job.getExpiryDate() == null) {
            job.setExpiryDate(LocalDateTime.now().plusDays(14));
        }

        if (job.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        if (req.getSkillIds() != null && !req.getSkillIds().isEmpty()) {
            List<Skill> skills = skillRepository.findAllById(req.getSkillIds());
            if (skills.size() != req.getSkillIds().size()) {
                throw new AppException(ErrorCode.SKILL_NOT_FOUND);
            }
            job.setRequiredSkills(new HashSet<>(skills));
        } else {
            job.setRequiredSkills(new HashSet<>());
        }

        Job saved = jobRepository.save(job);
        return jobMapper.toJobResponse(saved);
    }

    public PageResponse<JobResponse> getAllJobs(int page, int size, String search, String city, Boolean urgent) {
        Sort sort = Sort.by("postedAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        LocalDateTime now = LocalDateTime.now();
        Page<Job> jobPage;

        //  Nếu là "tuyển gấp" thì lấy job đăng trong 7 ngày và sắp hết hạn trong 3 ngày tới
        if (Boolean.TRUE.equals(urgent)) {
            LocalDateTime postedCutoff = now.minusDays(7);
            LocalDateTime urgentDeadline = now.plusDays(3);
            jobPage = jobRepository.findUrgentJobs(postedCutoff, urgentDeadline, pageable);

        } else {
            //  Nếu không phải "tuyển gấp" → logic cũ (mới nhất, 30 ngày)
            LocalDateTime cutoff = now.minusDays(30);

            if (search != null && !search.trim().isEmpty() && city != null && !city.trim().isEmpty()) {
                jobPage = jobRepository.searchActiveJobsByCity(search.trim(), city.trim(), cutoff, pageable);
            } else if (city != null && !city.trim().isEmpty()) {
                jobPage = jobRepository.findActiveJobsByCity(city.trim(), cutoff, pageable);
            } else if (search != null && !search.trim().isEmpty()) {
                jobPage = jobRepository.searchActiveJobs(search.trim(), cutoff, pageable);
            } else {
                jobPage = jobRepository.findActiveJobs(cutoff, pageable);
            }
        }

        List<JobResponse> jobResponses = jobPage.getContent()
                .stream()
                .map(jobMapper::toJobResponse)
                .toList();

        return PageResponse.<JobResponse>builder()
                .currentPage(jobPage.getNumber() + 1)
                .totalPages(jobPage.getTotalPages())
                .pageSize(jobPage.getSize())
                .totalElements(jobPage.getTotalElements())
                .data(jobResponses)
                .build();
    }




    public JobResponse getJobById(String id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));
        return jobMapper.toJobResponse(job);
    }

    @Transactional
    public JobResponse updateJob(String id, JobUpdateRequest req) {
        // Tìm job cần cập nhật
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        // Cập nhật các trường cơ bản
        jobMapper.updateJob(job, req);

        // Nếu có danh sách kỹ năng mới → cập nhật lại
        if (req.getSkillIds() != null) {
            List<Skill> skills = skillRepository.findAllById(req.getSkillIds());

            if (skills.size() != req.getSkillIds().size()) {
                throw new AppException(ErrorCode.SKILL_NOT_FOUND);
            }

            job.setRequiredSkills(new HashSet<>(skills));
        }

        // Lưu job
        Job updated = jobRepository.save(job);
        return jobMapper.toJobResponse(updated);
    }


    public void deleteJob(String id) {
        if (!jobRepository.existsById(id)) {
            throw new AppException(ErrorCode.JOB_NOT_FOUND);
        }
        jobRepository.deleteById(id);
    }

    @Transactional
    public Job addRequiredSkillsToJob(String jobId, List<String> skillNames) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        Set<Skill> skills = skillNames.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(name -> skillRepository.findByNameIgnoreCase(name)
                        .orElseGet(() -> skillRepository.save(
                                Skill.builder().name(name).build()
                        )))
                .collect(Collectors.toSet());

        job.getRequiredSkills().addAll(skills);
        return jobRepository.save(job);
    }

    @Transactional
    public void removeRequiredSkill(String jobId, String skillName) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        job.getRequiredSkills().removeIf(skill ->
                skill.getName().equalsIgnoreCase(skillName));

        jobRepository.save(job);
    }

    public List<JobCategoryCount> getJobCategoryCounts() {
        List<Object[]> results = jobRepository.countJobsByCategory();

        return results.stream()
                .map(r -> new JobCategoryCount(
                        r[0] != null ? r[0].toString() : "UNKNOWN",
                        (Long) r[1]
                ))
                .toList();
    }

}

