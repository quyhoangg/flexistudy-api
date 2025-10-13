package com.quyhoang.flexistudy.service.impl;

import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    @Value("${app.file.storage-dir}")
    String storageDir;

    @Value("${app.file.download-prefix}")
    String urlPrefix;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        try {
            if (file == null || file.isEmpty()) {
                throw new AppException(ErrorCode.INVALID_REQUEST);
            }

            // Tạo thư mục con (avatars / logos / cv)
            Path uploadPath = Paths.get(storageDir, folder);
            Files.createDirectories(uploadPath);

            // Tạo tên file an toàn
            String original = (file.getOriginalFilename() == null) ? "unknown" : file.getOriginalFilename();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = UUID.randomUUID() + "_" + timestamp + "_" + original;
            Path filePath = uploadPath.resolve(filename);

            // Lưu file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về URL public
            String fileUrl = (urlPrefix.endsWith("/"))
                    ? (urlPrefix + folder + "/" + filename)
                    : (urlPrefix + "/" + folder + "/" + filename);
            log.info(" Uploaded file to {}", fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error(" Upload failed", e);
            throw new AppException(ErrorCode.UPLOAD_FAILE);
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isBlank()) return false;
            String pathPart = java.net.URI.create(fileUrl).getPath();
            String fileName = Paths.get(pathPart).getFileName().toString();

            // Lấy folder (avatars, logos, cv)
            String folder = Paths.get(pathPart).getParent().getFileName().toString();
            Path filePath = Paths.get(storageDir, folder, fileName);

            Files.deleteIfExists(filePath);
            log.info(" Deleted file {}", filePath);
            return true;
        } catch (Exception e) {
            log.warn("Cannot delete file: {}", e.getMessage());
            return false;
        }
    }
}
