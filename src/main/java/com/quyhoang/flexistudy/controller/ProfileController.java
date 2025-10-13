package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.configuration.SecurityUtils;
import com.quyhoang.flexistudy.dto.request.ApiResponse;
import com.quyhoang.flexistudy.dto.request.ProfileUpdateRequest;
import com.quyhoang.flexistudy.dto.response.ProfileResponse;
import com.quyhoang.flexistudy.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {
    ProfileService profileService;
    SecurityUtils securityUtils;
    HttpServletRequest httpRequest;

    @GetMapping("/me")
    public ApiResponse<ProfileResponse> getMyProfile() {
        String userId = securityUtils.extractUserIdFromToken(httpRequest);
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.getProfile(userId))
                .message("Fetched profile successfully")
                .build();
    }

    @PutMapping("/me")
    public ApiResponse<ProfileResponse> updateMyProfile(@RequestBody ProfileUpdateRequest req) {
        String userId = securityUtils.extractUserIdFromToken(httpRequest);
        return ApiResponse.<ProfileResponse>builder()
                .result(profileService.updateProfile(userId, req))
                .message("Profile updated successfully")
                .build();
    }
}
