package com.quyhoang.flexistudy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quyhoang.flexistudy.dto.request.PayOSCreateRequest;
import com.quyhoang.flexistudy.dto.response.PayOSCreateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
public class PayOSService {

    @Value("${payos.client-id}")
    private String clientId;

    @Value("${payos.api-key}")
    private String apiKey;

    @Value("${payos.checksum-key}")
    private String checksumKey;

    @Value("${payos.api-url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();


    public PayOSCreateResponse createPaymentLink(PayOSCreateRequest request) {
        try {

            String signature = generateSignature(request);
            request.setSignature(signature);


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-client-id", clientId);
            headers.set("x-api-key", apiKey);


            HttpEntity<PayOSCreateRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<PayOSCreateResponse> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, entity, PayOSCreateResponse.class);

            return response.getBody();

        } catch (Exception e) {
            log.error("❌ Lỗi khi tạo link thanh toán PayOS: ", e);
            return null;
        }
    }


    private String generateSignature(PayOSCreateRequest request) throws Exception {
        SortedMap<String, String> sortedParams = new TreeMap<>();
        sortedParams.put("amount", String.valueOf(request.getAmount()));
        sortedParams.put("cancelUrl", request.getCancelUrl());
        sortedParams.put("description", request.getDescription());
        sortedParams.put("orderCode", String.valueOf(request.getOrderCode()));
        sortedParams.put("returnUrl", request.getReturnUrl());

        StringBuilder rawData = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (rawData.length() > 0) rawData.append("&");
            rawData.append(entry.getKey()).append("=").append(entry.getValue());
        }

        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(checksumKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(secretKey);
        byte[] bytes = hmac.doFinal(rawData.toString().getBytes(StandardCharsets.UTF_8));

        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            hash.append(String.format("%02x", b));
        }

        return hash.toString();
    }
}
