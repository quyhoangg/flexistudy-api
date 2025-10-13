package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.configuration.JwtUtils;
import com.quyhoang.flexistudy.configuration.SecurityUtils;
import com.quyhoang.flexistudy.dto.request.ApiResponse;
import com.quyhoang.flexistudy.dto.request.ApplicationCreateRequest;
import com.quyhoang.flexistudy.dto.request.ApplicationUpdateRequest;
import com.quyhoang.flexistudy.dto.response.ApplicationResponse;
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
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationService {
    ApplicationRepository applicationRepository;
    JobRepository jobRepository;
    UserRepository userRepository;
    FileStorageService  fileStorageService;
    SecurityUtils securityUtils;
    HttpServletRequest httpRequest;
    JwtUtils jwtUtils;


    @Transactional
    public Application create(ApplicationCreateRequest req) {
        // 🔹 1. Lấy userId từ JWT
        String userId = securityUtils.extractUserIdFromToken(httpRequest);
        if (userId == null)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        // 🔹 2. Kiểm tra user đã apply job này chưa
        boolean alreadyApplied = applicationRepository.existsByUser_IdAndJob_Id(userId, req.getJobId());
        if (alreadyApplied)
            throw new AppException(ErrorCode.APPLIED_ALREADY);

        // 🔹 3. Kiểm tra job có tồn tại và còn mở không
        Job job = jobRepository.findById(req.getJobId())
                .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_FOUND));

        if (job.getStatus() == JobStatus.CLOSED)
            throw new AppException(ErrorCode.JOB_CLOSED);

        // 🔹 4. Lấy user hiện tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 🔹 5. Tạo mới Application
        Application app = new Application();
        app.setJob(job);
        app.setUser(user);
        app.setCompany(job.getCompany());
        app.setFullName(req.getFullName());
        app.setEmail(req.getEmail());
        app.setPhone(req.getPhone());
        app.setCvUrl(req.getCvUrl());
        app.setCoverLetter(req.getCoverLetter());
        app.setStatus(ApplicationStatus.PENDING);

        // 🔹 6. Lưu vào DB
        Application saved = applicationRepository.save(app);

        // 🔹 7. Log ra console/server
        log.info("✅ User [{}] applied for job [{}] at company [{}]",
                user.getId(), job.getId(), job.getCompany().getName());

        return saved;
    }


    public String uploadCv(MultipartFile file) {
        return fileStorageService.uploadFile(file, "cv");
    }

    public List<Application> getApplicationsForRecruiter(HttpServletRequest request) {
        // 🔹 Lấy recruiterId từ token
        String recruiterId = jwtUtils.getUserIdFromRequest(request);

        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (recruiter.getCompany() == null) {
            throw new AppException(ErrorCode.RECRUITER_NO_COMPANY);
        }

        String companyId = recruiter.getCompany().getId();

        // 🔹 Trả danh sách Application theo job thuộc công ty này
        return applicationRepository.findByJob_Company_Id(companyId);
    }

    public List<Application> getApplicationsByCompany(String companyId) {
        return applicationRepository.findByCompany_Id(companyId);
    }

    @Transactional
    public Application updateStatus(String id, ApplicationUpdateRequest req) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_FOUND));

        app.setStatus(req.getStatus());
        if (req.getNote() != null && !req.getNote().isBlank()) {
            app.setNote(req.getNote());
        }

        Application updated = applicationRepository.save(app);
        log.info("🔄 Application {} status updated to {}", id, req.getStatus());
        return updated;
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
        if (!applicationRepository.existsById(id)) {
            throw new AppException(ErrorCode.APPLICATION_NOT_FOUND);
        }
        applicationRepository.deleteById(id);
        log.info("🗑️ Application {} deleted", id);
    }
}
