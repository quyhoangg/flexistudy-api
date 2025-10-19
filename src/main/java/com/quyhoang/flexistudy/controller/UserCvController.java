package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.ApiResponse;
import com.quyhoang.flexistudy.dto.response.UserCvResponse;
import com.quyhoang.flexistudy.service.UserCvService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cv")
@RequiredArgsConstructor
public class UserCvController {

    UserCvService userCvService;

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.warn(" Không có Authentication trong context");
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            Object claim = jwt.getClaim("userId");
            if (claim != null) {
                return claim.toString();
            }
        }

        // fallback nếu không có claim
        log.warn(" JWT không chứa userId claim, dùng name fallback");
        return authentication.getName();
    }

    @PostMapping("/upload")
    public ApiResponse<UserCvResponse> uploadCv(@RequestParam("file") MultipartFile file) {
        String userId = getCurrentUserId();
        UserCvResponse result = userCvService.uploadCv(userId, file);
        return ApiResponse.<UserCvResponse>builder()
                .message("Tải CV lên thành công")
                .result(result)
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserCvResponse>> getUserCvs() {
        String userId = getCurrentUserId();
        List<UserCvResponse> list = userCvService.getUserCvs(userId);
        return ApiResponse.<List<UserCvResponse>>builder()
                .message("Lấy danh sách CV thành công")
                .result(list)
                .build();
    }


    @DeleteMapping("/{cvId}")
    public ApiResponse<Void> deleteCv(@PathVariable String cvId) {
        String userId = getCurrentUserId();
        userCvService.deleteCv(userId, cvId);
        return ApiResponse.<Void>builder()
                .message("Đã xóa CV thành công")
                .build();
    }

    @PutMapping("/{cvId}/primary")
    public ApiResponse<UserCvResponse> setPrimary(@PathVariable String cvId) {
        String userId = getCurrentUserId();
        UserCvResponse result = userCvService.setPrimaryCv(userId, cvId);
        return ApiResponse.<UserCvResponse>builder()
                .message("Cập nhật CV chính thành công")
                .result(result)
                .build();
    }
}
