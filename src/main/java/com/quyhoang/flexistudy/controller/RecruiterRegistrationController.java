package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.ApiResponse;
import com.quyhoang.flexistudy.dto.request.RecruiterRegistrationRequest;
import com.quyhoang.flexistudy.dto.response.RecruiterResponse;
import com.quyhoang.flexistudy.service.RecruiterRegistrationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hr")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecruiterRegistrationController {
    RecruiterRegistrationService recruiterRegistrationService;

    @PostMapping("/register")
    public ApiResponse<RecruiterResponse> registerRecruiter(
            @Valid @RequestBody RecruiterRegistrationRequest request) {
        RecruiterResponse response = recruiterRegistrationService.registerRecruiter(request);
        return ApiResponse.<RecruiterResponse>builder()
                .message("Đăng ký nhà tuyển dụng thành công")
                .result(response)
                .build();
    }
}
