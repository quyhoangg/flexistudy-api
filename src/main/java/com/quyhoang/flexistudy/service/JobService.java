package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.JobCategoryCount;
import com.quyhoang.flexistudy.dto.PageResponse;
import com.quyhoang.flexistudy.dto.request.JobCreationRequest;
import com.quyhoang.flexistudy.dto.request.JobUpdateRequest;
import com.quyhoang.flexistudy.dto.response.JobResponse;
import com.quyhoang.flexistudy.entity.Company;
import com.quyhoang.flexistudy.entity.Job;
import com.quyhoang.flexistudy.entity.JobShift;
import com.quyhoang.flexistudy.entity.Skill;
import com.quyhoang.flexistudy.enums.EmployeeType;
import com.quyhoang.flexistudy.enums.JobStatus;
import com.quyhoang.flexistudy.enums.VerificationStatus;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.mapper.JobMapper;
import com.quyhoang.flexistudy.repository.CompanyRepository;
import com.quyhoang.flexistudy.repository.JobRepository;
import com.quyhoang.flexistudy.repository.JobShiftRepository;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobService {
    JobRepository jobRepository;
    CompanyRepository companyRepository;
    SkillRepository skillRepository;
    JobMapper jobMapper;
    JobShiftRepository jobShiftRepository;

    @Transactional
    public JobResponse createJob(JobCreationRequest req) {
        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

        Job job = jobMapper.toJob(req);
        job.setCompany(company);
        job.setStatus(JobStatus.CLOSED);

        // default expiry
        if (job.getExpiryDate() == null) {
            job.setExpiryDate(LocalDateTime.now().plusDays(14));
        }
        if (job.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        if (req.getQuantity() == null || req.getQuantity() < 1)
            job.setQuantity(1);
        else
            job.setQuantity(req.getQuantity());

        job.setIsActive(req.getIsActive() != null ? req.getIsActive() : true);

        if (company.getVerificationStatus() != VerificationStatus.VERIFIED) {
            job.setStatus(JobStatus.CLOSED); // hoặc CLOSED
            job.setIsActive(false);
        } else {
            // cho phép theo req, hoặc mặc định CLOSED đến khi recruiter "Publish"
            job.setStatus(JobStatus.CLOSED);
            job.setIsActive(true);
        }

        // resolve skills theo TÊN (atomic trong 1 transaction)
        if (req.getSkillNames() != null && !req.getSkillNames().isEmpty()) {
            Set<Skill> skills = req.getSkillNames().stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(name -> skillRepository.findByNameIgnoreCase(name)
                            .orElseGet(() -> skillRepository.save(Skill.builder().name(name).build())))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            job.setRequiredSkills(skills);
        } else {
            job.setRequiredSkills(new HashSet<>());
        }

        // tạo JobShift kèm Job
        if (req.getJobShifts() != null && !req.getJobShifts().isEmpty()) {
            List<JobShift> shifts = req.getJobShifts().stream()
                    .map(s -> JobShift.builder()
                            .date(s.getDate() != null ? s.getDate() : LocalDate.of(1970, 1, 1))
                            .startTime(s.getStartTime())
                            .endTime(s.getEndTime())
                            .description(s.getDescription())
                            .job(job)
                            .build())
                    .toList();
            job.setJobShifts(shifts);
        }

        Job saved = jobRepository.save(job);
        return jobMapper.toJobResponse(saved);
    }


    public PageResponse<JobResponse> getAllJobs(
            int page, int size,
            String search,
            String city,
            Boolean urgent,
            EmployeeType type,
            Integer minSalary,
            Integer maxSalary,
            boolean isAdmin,
            JobStatus status
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by("postedAt").descending());
        LocalDateTime now = LocalDateTime.now();

        // Bắt đầu với spec rỗng
        Specification<Job> spec = Specification.where(null);

        // Scope: user chỉ thấy OPEN; admin thấy tất cả
        if (!isAdmin) {
            spec = spec.and(JobSpecs.statusOpen());
        } else {
            spec = spec.and(JobSpecs.statusEquals(status));
        }

        // Urgent filter (nếu bật)
        if (Boolean.TRUE.equals(urgent)) {
            // ví dụ policy: urgent=true và đăng trong 7 ngày gần đây
            spec = spec.and(JobSpecs.urgentIs(true))
                    .and(JobSpecs.postedSince(now.minusDays(7)));
        } else {
            // Chỉ user mới bị giới hạn 30 ngày; admin không giới hạn
            if (!isAdmin) {
                spec = spec.and(JobSpecs.postedSince(now.minusDays(30)));
            }
        }

        // Optional filters (null-safe)
        spec = spec.and(JobSpecs.matchesSearch(search))
                .and(JobSpecs.cityEqualsIgnoreCase(city))
                .and(JobSpecs.typeEquals(type))
                .and(JobSpecs.salaryBetween(minSalary, maxSalary));

        Page<Job> jobPage = jobRepository.findAll(spec, pageable);

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


    public PageResponse<JobResponse> getJobsByCategory(
            String category,
            int page,
            int size,
            String search,
            String city,
            EmployeeType type,
            Integer minSalary,
            Integer maxSalary,
            Boolean urgent
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("postedAt").descending());

        Specification<Job> spec = Specification
                .where(JobSpecs.categoryEquals(category))
                .and(JobSpecs.statusOpen())
                .and(JobSpecs.matchesSearch(search))
                .and(JobSpecs.cityEquals(city))
                .and(JobSpecs.typeEquals(type))
                .and(JobSpecs.salaryBetween(minSalary, maxSalary))
                .and(JobSpecs.postedSince(LocalDateTime.now().minusDays(30)));

        Page<Job> jobPage = jobRepository.findAll(spec, pageable);

        List<JobResponse> jobResponses = jobPage.getContent().stream()
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

        if (req.getQuantity() != null && req.getQuantity() > 0) {
            job.setQuantity(req.getQuantity());
        }
        if (req.getIsActive() != null) {
            job.setIsActive(req.getIsActive());
        }

        // Nếu có danh sách kỹ năng mới → cập nhật lại
        if (req.getSkillIds() != null) {
            List<Skill> skills = skillRepository.findAllById(req.getSkillIds());

            if (skills.size() != req.getSkillIds().size()) {
                throw new AppException(ErrorCode.SKILL_NOT_FOUND);
            }

            job.setRequiredSkills(new HashSet<>(skills));
        }

        if (req.getJobShifts() != null) {
            job.getJobShifts().clear();
            List<JobShift> newShifts = req.getJobShifts().stream()
                    .map(s -> JobShift.builder()
                            .date(s.getDate())
                            .startTime(s.getStartTime())
                            .endTime(s.getEndTime())
                            .description(s.getDescription())
                            .job(job)
                            .build())
                    .toList();
            job.getJobShifts().addAll(newShifts);
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

