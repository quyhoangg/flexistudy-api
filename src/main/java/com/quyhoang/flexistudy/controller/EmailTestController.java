package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.ApiResponse;
import com.quyhoang.flexistudy.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    @GetMapping("/send-mail")
    public ApiResponse<String> sendMail(@RequestParam String to) {
        emailService.sendOtpEmail(
                to,
                "123456",
                "https://flexistudy.vn/verify?otp=123456"
        );
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Email OTP sent successfully to " + to)
                .build();
    }

    @GetMapping("/welcome")
    public ApiResponse<String> sendWelcome(@RequestParam String to) {
        emailService.sendWelcomeEmail(
                to,
                "Quý Hoàng",
                "https://flexistudy.vn/welcome"
        );
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Welcome email sent successfully to " + to)
                .build();
    }

    @GetMapping("/forgot-password")
    public ApiResponse<String> sendForgot(@RequestParam String to) {
        emailService.sendForgotPasswordEmail(
                to,
                "Quý Hoàng",
                "https://flexistudy.vn/reset-password?token=demo"
        );
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Forgot password email sent successfully to " + to)
                .build();
    }
}
