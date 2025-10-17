package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.ApiResponse;
import com.quyhoang.flexistudy.dto.request.JobShiftCreateRequest;
import com.quyhoang.flexistudy.dto.request.JobShiftUpdateRequest;
import com.quyhoang.flexistudy.dto.response.JobShiftResponse;
import com.quyhoang.flexistudy.entity.JobShift;
import com.quyhoang.flexistudy.mapper.JobShiftMapper;
import com.quyhoang.flexistudy.service.JobShiftService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job-shifts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobShiftController {
    JobShiftService jobShiftService;
    JobShiftMapper mapper;

    @PostMapping
    public ApiResponse<JobShiftResponse> create(@Valid @RequestBody JobShiftCreateRequest req) {
        JobShift shift = jobShiftService.create(req);
        return ApiResponse.<JobShiftResponse>builder()
                .result(mapper.toResponse(shift))
                .build();
    }

    @PatchMapping("/{id}")
    public ApiResponse<JobShiftResponse> update(@PathVariable String id,
                                                @Valid @RequestBody JobShiftUpdateRequest req) {
        JobShift shift = jobShiftService.update(id, req);
        return ApiResponse.<JobShiftResponse>builder()
                .result(mapper.toResponse(shift))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<JobShiftResponse> getById(@PathVariable String id) {
        return ApiResponse.<JobShiftResponse>builder()
                .result(mapper.toResponse(jobShiftService.getById(id)))
                .build();
    }

    @GetMapping("/job/{jobId}")
    public ApiResponse<List<JobShiftResponse>> getByJob(@PathVariable String jobId) {
        List<JobShiftResponse> list = jobShiftService.getByJob(jobId)
                .stream().map(mapper::toResponse).toList();

        return ApiResponse.<List<JobShiftResponse>>builder()
                .result(list)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        jobShiftService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Shift deleted successfully")
                .build();
    }
}
