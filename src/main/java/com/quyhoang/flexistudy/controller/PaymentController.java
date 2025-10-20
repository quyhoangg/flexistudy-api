package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.request.PayOSCallbackRequest;
import com.quyhoang.flexistudy.dto.request.PayOSCreateRequest;
import com.quyhoang.flexistudy.dto.response.PayOSCreateResponse;
import com.quyhoang.flexistudy.service.PayOSService;
import com.quyhoang.flexistudy.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PayOSService payOSService;
    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody PayOSCreateRequest request) {
        PayOSCreateResponse response = payOSService.createPaymentLink(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/callback")
    public ResponseEntity<?> handleCallback(@RequestBody PayOSCallbackRequest payload) {
        if (!"success".equalsIgnoreCase(payload.getStatus())) {
            return ResponseEntity.ok("Payment not successful, no record created");
        }
        paymentService.createSuccessPayment(payload);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(
            @RequestParam String code,
            @RequestParam String id,
            @RequestParam boolean cancel,
            @RequestParam String status,
            @RequestParam String orderCode) {
        return ResponseEntity.ok("Payment successful");
    }

}

