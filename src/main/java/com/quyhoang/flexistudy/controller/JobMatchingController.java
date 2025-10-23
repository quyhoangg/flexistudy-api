package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.configuration.JwtUtils;
import com.quyhoang.flexistudy.dto.JobMatchResult;
import com.quyhoang.flexistudy.dto.ApiResponse;
import com.quyhoang.flexistudy.service.JobMatchingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
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
    JwtUtils jwtUtils;

    @GetMapping("/match/me")
    public ApiResponse<List<JobMatchResult>> matchJobs(HttpServletRequest request) {
        String userId = jwtUtils.getUserIdFromRequest(request);

        return ApiResponse.<List<JobMatchResult>>builder()
                .result(jobMatchingService.findMatchingJobs(userId))
                .message("Gợi ý công việc theo hồ sơ thành công")
                .build();
    }
}

