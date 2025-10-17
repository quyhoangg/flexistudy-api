package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.ApiResponse;
import com.quyhoang.flexistudy.service.FileStorageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileUploadController {
    FileStorageService fileStorageService;

    @PostMapping("/cv")
    public ApiResponse<String> uploadCv(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.uploadFile(file, "cv");
        return ApiResponse.<String>builder()
                .result(url)
                .message("CV uploaded successfully")
                .build();
    }

}
