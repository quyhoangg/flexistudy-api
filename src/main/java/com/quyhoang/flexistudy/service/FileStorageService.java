package com.quyhoang.flexistudy.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String uploadFile(MultipartFile file, String folder);
    boolean deleteFile(String fileUrl);
}
