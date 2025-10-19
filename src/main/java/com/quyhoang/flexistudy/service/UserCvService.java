package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.response.UserCvResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface UserCvService {
    UserCvResponse uploadCv(String userId, MultipartFile file);
    List<UserCvResponse> getUserCvs(String userId);
    void deleteCv(String userId, String cvId);
    UserCvResponse setPrimaryCv(String userId, String cvId);
}
