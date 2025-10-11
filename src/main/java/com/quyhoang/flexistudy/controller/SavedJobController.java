package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.request.ApiResponse;
import com.quyhoang.flexistudy.entity.Job;
import com.quyhoang.flexistudy.service.SavedJobService;
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

    // ✅ Lưu job
    @PostMapping("/{userId}/{jobId}")
    public ApiResponse<Void> saveJob(@PathVariable String userId, @PathVariable String jobId) {
        savedJobService.saveJob(userId, jobId);
        return ApiResponse.<Void>builder()
                .message("Job saved successfully")
                .build();
    }

    // ✅ Bỏ lưu job
    @DeleteMapping("/{userId}/{jobId}")
    public ApiResponse<Void> unsaveJob(@PathVariable String userId, @PathVariable String jobId) {
        savedJobService.unsaveJob(userId, jobId);
        return ApiResponse.<Void>builder()
                .message("Job unsaved successfully")
                .build();
    }

    // ✅ Lấy danh sách job đã lưu của user
    @GetMapping("/{userId}")
    public ApiResponse<Set<Job>> getSavedJobs(@PathVariable String userId) {
        return ApiResponse.<Set<Job>>builder()
                .result(savedJobService.getSavedJobs(userId))
                .build();
    }
}
