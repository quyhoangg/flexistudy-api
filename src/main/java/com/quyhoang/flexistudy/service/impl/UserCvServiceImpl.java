package com.quyhoang.flexistudy.service.impl;

import com.quyhoang.flexistudy.dto.response.UserCvResponse;
import com.quyhoang.flexistudy.entity.UserCv;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.repository.UserCvRepository;
import com.quyhoang.flexistudy.service.FileStorageService;
import com.quyhoang.flexistudy.service.UserCvService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserCvServiceImpl implements UserCvService {
    FileStorageService fileStorageService;
    UserCvRepository userCvRepository;

    @Override
    public UserCvResponse uploadCv(String userId, MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new AppException(ErrorCode.INVALID_REQUEST);

        // kiểm tra định dạng
        String type = file.getContentType();
        if (!List.of("application/pdf", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document").contains(type))
            throw new AppException(ErrorCode.UNSUPPORTED_FILE_TYPE);

        if (file.getSize() > 5 * 1024 * 1024)
            throw new AppException(ErrorCode.FILE_TOO_LARGE);

        // upload lên thư mục "cv"
        String fileUrl = fileStorageService.uploadFile(file, "cv");

        // nếu user chưa có CV chính → đặt cái đầu tiên
        boolean primary = userCvRepository.findFirstByUserIdAndIsPrimaryTrue(userId).isEmpty();

        UserCv cv = userCvRepository.save(UserCv.builder()
                .userId(userId)
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .fileType(type)
                .fileSize(file.getSize())
                .isPrimary(primary)
                .uploadedAt(LocalDateTime.now())
                .build());

        return toDto(cv);
    }

    @Override
    public List<UserCvResponse> getUserCvs(String userId) {
        return userCvRepository.findByUserIdOrderByUploadedAtDesc(userId)
                .stream().map(this::toDto).toList();
    }

    @Override
    public void deleteCv(String userId, String cvId) {
        UserCv cv = userCvRepository.findById(cvId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if (!cv.getUserId().equals(userId))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        fileStorageService.deleteFile(cv.getFileUrl());
        userCvRepository.delete(cv);
    }

    @Override
    public UserCvResponse setPrimaryCv(String userId, String cvId) {
        UserCv cv = userCvRepository.findById(cvId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (!cv.getUserId().equals(userId))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        userCvRepository.findFirstByUserIdAndIsPrimaryTrue(userId)
                .ifPresent(old -> { old.setPrimary(false); userCvRepository.save(old); });

        cv.setPrimary(true);
        userCvRepository.save(cv);

        return toDto(cv);
    }

    private UserCvResponse toDto(UserCv cv) {
        return UserCvResponse.builder()
                .id(cv.getId())
                .fileName(cv.getFileName())
                .fileUrl(cv.getFileUrl())
                .fileType(cv.getFileType())
                .fileSize(cv.getFileSize())
                .uploadedAt(cv.getUploadedAt())
                .isPrimary(cv.isPrimary())
                .build();
    }
}
