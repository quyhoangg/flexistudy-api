package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.ApiResponse;
import com.quyhoang.flexistudy.dto.request.RoleRequest;
import com.quyhoang.flexistudy.dto.response.RoleResponse;
import com.quyhoang.flexistudy.enums.RoleName;
import com.quyhoang.flexistudy.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiResponse<Void> delete(@PathVariable RoleName role) {
        roleService.delete(role);
        return ApiResponse.<Void>builder().build();
    }
}
