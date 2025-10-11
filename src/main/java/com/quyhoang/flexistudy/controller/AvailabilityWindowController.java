package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.request.ApiResponse;
import com.quyhoang.flexistudy.dto.request.AvailabilityWindowCreateRequest;
import com.quyhoang.flexistudy.dto.request.AvailabilityWindowUpdateRequest;
import com.quyhoang.flexistudy.dto.response.AvailabilityWindowResponse;
import com.quyhoang.flexistudy.entity.AvailabilityWindow;
import com.quyhoang.flexistudy.mapper.AvailabilityWindowMapper;
import com.quyhoang.flexistudy.service.AvailabilityWindowService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/availability-windows")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AvailabilityWindowController {

    AvailabilityWindowService availabilityWindowService;
    AvailabilityWindowMapper mapper;

    @PostMapping
    public ApiResponse<AvailabilityWindowResponse> create(@Valid @RequestBody AvailabilityWindowCreateRequest req) {
        AvailabilityWindow window = availabilityWindowService.create(req);
        return ApiResponse.<AvailabilityWindowResponse>builder()
                .result(mapper.toResponse(window))
                .build();
    }

    @PatchMapping("/{id}")
    public ApiResponse<AvailabilityWindowResponse> update(@PathVariable String id,
                                                          @Valid @RequestBody AvailabilityWindowUpdateRequest req) {
        AvailabilityWindow window = availabilityWindowService.update(id, req);
        return ApiResponse.<AvailabilityWindowResponse>builder()
                .result(mapper.toResponse(window))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<AvailabilityWindowResponse> getById(@PathVariable String id) {
        return ApiResponse.<AvailabilityWindowResponse>builder()
                .result(mapper.toResponse(availabilityWindowService.getById(id)))
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<AvailabilityWindowResponse>> getByUser(@PathVariable String userId) {
        List<AvailabilityWindowResponse> list = availabilityWindowService.getByUser(userId)
                .stream().map(mapper::toResponse).toList();

        return ApiResponse.<List<AvailabilityWindowResponse>>builder()
                .result(list)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        availabilityWindowService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Availability window deleted successfully")
                .build();
    }
}
