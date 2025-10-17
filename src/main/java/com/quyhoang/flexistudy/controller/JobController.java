package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.JobCategoryCount;
import com.quyhoang.flexistudy.dto.PageResponse;
import com.quyhoang.flexistudy.dto.ApiResponse;
import com.quyhoang.flexistudy.dto.request.JobCreationRequest;
import com.quyhoang.flexistudy.dto.request.JobUpdateRequest;
import com.quyhoang.flexistudy.dto.response.JobResponse;
import com.quyhoang.flexistudy.enums.EmployeeType;
import com.quyhoang.flexistudy.enums.JobStatus;
import com.quyhoang.flexistudy.service.JobService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JobController {
    JobService jobService;

    @GetMapping("/by-category")
    public ApiResponse<PageResponse<JobResponse>> getJobsByCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) EmployeeType type,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) Integer maxSalary,
            @RequestParam(required = false) Boolean urgent
    ) {
        PageResponse<JobResponse> response = jobService.getJobsByCategory(
                category, page, size, search, city, type, minSalary, maxSalary, urgent
        );
        return ApiResponse.<PageResponse<JobResponse>>builder()
                .result(response)
                .build();
    }


    @PostMapping
    ApiResponse<JobResponse> createJob(@RequestBody  JobCreationRequest request) {
        return ApiResponse.<JobResponse>builder()
                .result(jobService.createJob(request))
                .build();
    }


    @GetMapping
    public ApiResponse<PageResponse<JobResponse>> getAllJobsForUser(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "false") boolean urgent,
            @RequestParam(required = false) EmployeeType type,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) Integer maxSalary
    ) {
        PageResponse<JobResponse> response = jobService.getAllJobs(
                page, size, search, city, urgent, type, minSalary, maxSalary, false, null
        );
        return ApiResponse.<PageResponse<JobResponse>>builder().result(response).build();
    }

    @GetMapping("/admin")
    public ApiResponse<PageResponse<JobResponse>> getAllJobsForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "false") boolean urgent,
            @RequestParam(required = false) EmployeeType type,
            @RequestParam(required = false) Integer minSalary,
            @RequestParam(required = false) Integer maxSalary,
            @RequestParam(required = false) JobStatus status
    ) {
        PageResponse<JobResponse> response = jobService.getAllJobs(
                page, size, search, city, urgent, type, minSalary, maxSalary, true, status
        );
        return ApiResponse.<PageResponse<JobResponse>>builder().result(response).build();
    }




    @GetMapping("/{id}")
    ApiResponse<JobResponse> getJobById(@PathVariable String id) {
        return ApiResponse.<JobResponse>builder()
                .result(jobService.getJobById(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<JobResponse> updateJob(@PathVariable String id,
                                       @RequestBody @Valid JobUpdateRequest request) {
        return ApiResponse.<JobResponse>builder()
                .result(jobService.updateJob(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteJob(@PathVariable String id) {
        jobService.deleteJob(id);
        return ApiResponse.<String>builder()
                .result("Job has been deleted")
                .build();
    }

    @PostMapping("/{jobId}/skills")
    public ApiResponse<Void> addJobSkills(
            @PathVariable String jobId,
            @RequestBody List<String> skills
    ) {
        jobService.addRequiredSkillsToJob(jobId, skills);
        return ApiResponse.<Void>builder()
                .message("Job skills updated successfully")
                .build();
    }

    @GetMapping("/categories")
    public ApiResponse<List<JobCategoryCount>> getJobCategories() {
        return ApiResponse.<List<JobCategoryCount>>builder()
                .result(jobService.getJobCategoryCounts())
                .build();
    }
}
