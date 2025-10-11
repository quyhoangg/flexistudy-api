package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.request.ApiResponse;
import com.quyhoang.flexistudy.dto.request.UserFullProfileRequest;
import com.quyhoang.flexistudy.service.UserProfileService;
import com.quyhoang.flexistudy.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpdateProfileController {

    UserProfileService userProfileService;

    @PostMapping
    public ApiResponse<Void> updateFullProfile(
            JwtAuthenticationToken auth,
            @Valid @RequestBody UserFullProfileRequest request
    ) {
        // ✅ Lấy userId từ JWT claim "userId"
        String userId = auth.getToken().getClaimAsString("userId");
        if (userId == null) {
            throw new RuntimeException("User ID not found in token");
        }

        userProfileService.updateFullProfile(userId, request);

        return ApiResponse.<Void>builder()
                .message("Cập nhật hồ sơ người dùng thành công")
                .build();
    }

    @GetMapping
    public ApiResponse<?> getFullProfile(JwtAuthenticationToken auth) {
        String userId = auth.getToken().getClaimAsString("userId");
        if (userId == null) {
            throw new RuntimeException("User ID not found in token");
        }

        return ApiResponse.<Object>builder()
                .result(userProfileService.getFullProfile(userId))
                .message("Lấy hồ sơ người dùng thành công")
                .build();
    }
}
