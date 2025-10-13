package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.configuration.JwtUtils;
import com.quyhoang.flexistudy.dto.request.ApiResponse;
import com.quyhoang.flexistudy.dto.response.JobResponse;
import com.quyhoang.flexistudy.service.SavedJobService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/saved-job")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SavedJobController {

    SavedJobService savedJobService;
    JwtUtils jwtUtils;

    @GetMapping("/check/{jobId}")
    public ApiResponse<Boolean> checkSavedJob(@PathVariable String jobId, HttpServletRequest request) {
        String userId = jwtUtils.getUserIdFromRequest(request);
        boolean saved = savedJobService.isJobSaved(userId, jobId);
        return ApiResponse.<Boolean>builder()
                .result(saved)
                .build();
    }

    @PostMapping("/{jobId}")
    public ApiResponse<Void> saveJob(@PathVariable String jobId, HttpServletRequest request) {
        String userId = jwtUtils.getUserIdFromRequest(request);
        savedJobService.saveJob(userId, jobId);
        return ApiResponse.<Void>builder()
                .message("Job saved successfully")
                .build();
    }

    @DeleteMapping("/{jobId}")
    public ApiResponse<Void> unsaveJob(@PathVariable String jobId, HttpServletRequest request) {
        String userId = jwtUtils.getUserIdFromRequest(request);
        savedJobService.unsaveJob(userId, jobId);
        return ApiResponse.<Void>builder()
                .message("Job unsaved successfully")
                .build();
    }

    @GetMapping
    public ApiResponse<Set<JobResponse>> getSavedJobs(HttpServletRequest request) {
        String userId = jwtUtils.getUserIdFromRequest(request);
        return ApiResponse.<Set<JobResponse>>builder()
                .result(savedJobService.getSavedJobs(userId))
                .build();
    }
}
