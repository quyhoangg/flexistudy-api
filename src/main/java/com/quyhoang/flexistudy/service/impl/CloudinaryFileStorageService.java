package com.quyhoang.flexistudy.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Primary
public class CloudinaryFileStorageService implements FileStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        try {
            if (file == null || file.isEmpty()) {
                throw new AppException(ErrorCode.INVALID_REQUEST);
            }

            Map<String, Object> options = ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "auto" // auto-detect image/video/pdf/etc
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            String url = (String) uploadResult.get("secure_url");
            log.info("Uploaded to Cloudinary: {}", url);
            return url;
        } catch (Exception e) {
            log.error("Upload failed", e);
            throw new AppException(ErrorCode.UPLOAD_FAILE);
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isBlank()) return false;

            // Extract public_id từ URL
            String[] parts = fileUrl.split("/");
            String publicIdWithExt = parts[parts.length - 1];
            String publicId = publicIdWithExt.substring(0, publicIdWithExt.lastIndexOf('.'));

            // Tìm folder
            String folder = parts[parts.length - 2];
            String fullPublicId = folder + "/" + publicId;

            Map result = cloudinary.uploader().destroy(fullPublicId, ObjectUtils.emptyMap());
            log.info("Deleted from Cloudinary: {}", fullPublicId);
            return true;
        } catch (Exception e) {
            log.warn("Cannot delete file from Cloudinary: {}", e.getMessage());
            return false;
        }
    }
}
