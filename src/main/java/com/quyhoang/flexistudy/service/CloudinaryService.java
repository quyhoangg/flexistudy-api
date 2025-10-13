package com.quyhoang.flexistudy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryService {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Value("${cloudinary.upload-preset}")
    private String uploadPreset;

    private final RestTemplate restTemplate = new RestTemplate();

    public String uploadImage(MultipartFile file) {
        try {
            String url = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/upload";

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());
            body.add("upload_preset", uploadPreset);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("secure_url")) {
                return (String) responseBody.get("secure_url");
            }
            throw new RuntimeException("Upload thất bại, không có URL trả về");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi upload Cloudinary: " + e.getMessage(), e);
        }
    }
}

