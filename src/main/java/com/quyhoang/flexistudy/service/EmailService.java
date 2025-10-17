package com.quyhoang.flexistudy.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendOtpEmail(String to, String otp, String verifyLink) {
        try {
            // 1️ Chuẩn bị context cho Thymeleaf
            Context context = new Context();
            context.setVariable("otp", otp);
            context.setVariable("verifyLink", verifyLink);

            // 2️ Render template thành HTML
            String htmlContent = templateEngine.process("email/otp-email.html", context);

            // 3️ Tạo mail dạng HTML
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Xác nhận đăng nhập FlexiStudy");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("✅ Gửi email OTP thành công đến {}", to);

        } catch (Exception e) {
            log.error(" Gửi email OTP thất bại: {}", e.getMessage());
            throw new RuntimeException("Không thể gửi email OTP", e);
        }
    }

    public void sendWelcomeEmail(String to, String userName, String getStartedLink) {
        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("getStartedLink", getStartedLink);
        String html = templateEngine.process("email/welcome-email.html", context);
        sendHtmlEmail(to, "Chào mừng đến với FlexiStudy 🎓", html);
    }

    public void sendForgotPasswordEmail(String to, String userName, String otp) {
        try {
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("otp", otp);
            context.setVariable("validMinutes", 5);

            String html = templateEngine.process("email/forgot-password-email.html", context);

            sendHtmlEmail(to, "Khôi phục mật khẩu FlexiStudy 🔑", html);
            log.info("✅ Email quên mật khẩu đã gửi tới {}", to);
        } catch (Exception e) {
            log.error("❌ Gửi email quên mật khẩu thất bại: {}", e.getMessage());
            throw new RuntimeException("Không thể gửi email khôi phục mật khẩu");
        }
    }



    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.info(" Sent HTML mail to {}", to);
        } catch (Exception e) {
            log.error(" Failed to send HTML mail: {}", e.getMessage());
            throw new RuntimeException("Không thể gửi mail", e);
        }
    }

}
