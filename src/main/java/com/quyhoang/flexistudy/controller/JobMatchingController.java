package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.JobMatchResult;
import com.quyhoang.flexistudy.dto.request.ApiResponse;
import com.quyhoang.flexistudy.service.JobMatchingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobMatchingController {

    JobMatchingService jobMatchingService;

    @GetMapping("/match/{userId}")
    public ApiResponse<List<JobMatchResult>> matchJobs(@PathVariable String userId) {
        return ApiResponse.<List<JobMatchResult>>builder()
                .result(jobMatchingService.findMatchingJobs(userId))
                .build();
    }
}
