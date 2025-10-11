package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.request.ApiResponse;
import com.quyhoang.flexistudy.dto.request.ApplicationCreateRequest;
import com.quyhoang.flexistudy.dto.request.ApplicationUpdateRequest;
import com.quyhoang.flexistudy.dto.response.ApplicationResponse;
import com.quyhoang.flexistudy.entity.Application;
import com.quyhoang.flexistudy.mapper.ApplicationMapper;
import com.quyhoang.flexistudy.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationController {
    ApplicationService service;
    ApplicationMapper mapper;

    @PostMapping
    public ApiResponse<ApplicationResponse> create(@Valid @RequestBody ApplicationCreateRequest req) {
        Application app = service.create(req);
        return ApiResponse.<ApplicationResponse>builder()
                .result(mapper.toResponse(app))
                .build();
    }

    @PatchMapping("/{id}")
    public ApiResponse<ApplicationResponse> updateStatus(@PathVariable String id,
                                                         @Valid @RequestBody ApplicationUpdateRequest req) {
        Application app = service.updateStatus(id, req);
        return ApiResponse.<ApplicationResponse>builder()
                .result(mapper.toResponse(app))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ApplicationResponse> getById(@PathVariable String id) {
        return ApiResponse.<ApplicationResponse>builder()
                .result(mapper.toResponse(service.getById(id)))
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<ApplicationResponse>> getByUser(@PathVariable String userId) {
        List<ApplicationResponse> list = service.getAllByUser(userId)
                .stream().map(mapper::toResponse).toList();

        return ApiResponse.<List<ApplicationResponse>>builder()
                .result(list)
                .build();
    }

    @GetMapping("/job/{jobId}")
    public ApiResponse<List<ApplicationResponse>> getByJob(@PathVariable String jobId) {
        List<ApplicationResponse> list = service.getAllByJob(jobId)
                .stream().map(mapper::toResponse).toList();

        return ApiResponse.<List<ApplicationResponse>>builder()
                .result(list)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ApiResponse.<Void>builder()
                .message("Application deleted successfully")
                .build();
    }
}
