package com.quyhoang.flexistudy.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.quyhoang.flexistudy.dto.request.*;
import com.quyhoang.flexistudy.dto.response.AuthenticationResponse;
import com.quyhoang.flexistudy.dto.response.IntrospectResponse;
import com.quyhoang.flexistudy.entity.InvalidatedToken;
import com.quyhoang.flexistudy.entity.Role;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.entity.VerificationCode;
import com.quyhoang.flexistudy.enums.RoleName;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.mapper.UserMapper;
import com.quyhoang.flexistudy.repository.InvalidatedTokenRepository;
import com.quyhoang.flexistudy.repository.RoleRepository;
import com.quyhoang.flexistudy.repository.UserRepository;
import com.quyhoang.flexistudy.repository.VerificationCodeRepository;
import com.quyhoang.flexistudy.repository.httpClient.OutboundIdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    OutboundIdentityClient outboundIdentityClient;
    InvalidatedTokenRepository  invalidatedTokenRepository;
    EmailService emailService;
    VerificationCodeRepository verificationCodeRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;


    @Value("${jwt.signer-key}")
    @NonFinal
    private String SIGNER_KEY;

    @Value("${jwt.valid-duration}")
    @NonFinal
    private long VALID_DURATION;

    @Value("${jwt.refreshable-duration}")
    @NonFinal
    private long REFRESHABLE_DURATION;

    @NonFinal
    @Value("${outbound.identity.client-id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${outbound.identity.client-secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${outbound.identity.redirect-uri}")
    protected String REDIRECT_URI;

    @NonFinal
    protected final String GRANT_TYPE = "authorization_code";

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse outboundAuthenticate(String code) {
        // 1 Đổi code -> access token từ Google
        var response = outboundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(GRANT_TYPE)
                .build());

        String googleAccessToken = response.getAccessToken();

        // Lấy user info từ Google
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(googleAccessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> userInfoResponse = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        Map<String, Object> userInfo = userInfoResponse.getBody();
        String email = (String) userInfo.get("email");
        String fullName = (String) userInfo.get("name");
        String avatar = (String) userInfo.get("picture");
        String googleSub = (String) userInfo.get("id");

        log.info("GOOGLE USER INFO: {}", userInfo);

        // 3️⃣ Tìm hoặc tạo user mới
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // 👉 Lấy ROLE_USER mặc định
            Role defaultRole = roleRepository.findByName(RoleName.USER)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

            // 👉 Tách họ và tên (optional, tránh null)
            String firstName = "";
            String lastName = "";
            if (fullName != null && fullName.contains(" ")) {
                String[] parts = fullName.trim().split(" ", 2);
                firstName = parts[0];
                lastName = parts.length > 1 ? parts[1] : "";
            }

            try {
                user = User.builder()
                        .email(email)
                        .fullName(fullName)
                        .firstName(firstName)
                        .lastName(lastName)
                        .avatarUrl(avatar)
                        .googleSub(googleSub)
                        .provider("GOOGLE") // chuẩn hóa viết hoa
                        .username(email)
                        .emailVerified(false)
                        .roles(Set.of(defaultRole)) // Gán role mặc định
                        .password(null)
                        .build();

                userRepository.save(user);
                log.info("New Google user created with ROLE_USER: {}", email);
            } catch (DataIntegrityViolationException e) {
                log.warn("Duplicate email detected, fallback to existing user record.");
                user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            }
        }

        // 4 Gửi OTP nếu chưa xác minh email
        boolean emailVerificationRequired = !Boolean.TRUE.equals(user.isEmailVerified());
        log.info(">>> EMAIL VERIFIED: {} | emailVerificationRequired: {}", user.isEmailVerified(), emailVerificationRequired);
        if (emailVerificationRequired) {
            String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
            VerificationCode verification = VerificationCode.builder()
                    .email(user.getEmail())
                    .code(otp)
                    .expiresAt(LocalDateTime.now().plusMinutes(10))
                    .build();
            verificationCodeRepository.save(verification);

            emailService.sendOtpEmail(
                    user.getEmail(),
                    otp,
                    "https://flexistudy.vn/verify?email=" + user.getEmail() + "&otp=" + otp
            );
        }

        // 5 Sinh token JWT
        String accessToken = generateToken(user);

        return AuthenticationResponse.builder()
                .token(accessToken)
                .emailVerificationRequired(emailVerificationRequired)
                .email(user.getEmail())
                .authenticated(true)
                .build();
    }


    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Tạo OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        verificationCodeRepository.save(VerificationCode.builder()
                .email(email)
                .code(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build());

        // Gửi mail
        emailService.sendForgotPasswordEmail(email, user.getFullName(), otp);
    }

    public void resetPassword(ResetPasswordRequest request) {
        var user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Mã hóa password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }


    public void verifyForgotOtp(VerifyOtpRequest request) {
        var verification = verificationCodeRepository.findByEmailAndCode(
                request.getEmail().trim().toLowerCase(),
                request.getOtp().trim()
        ).orElseThrow(() -> new AppException(ErrorCode.INVALID_OTP));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.EXPIRED_OTP);
        }

        // Mark verified hoặc xóa luôn để tránh reuse
        verificationCodeRepository.delete(verification);
    }


    @Transactional
    public AuthenticationResponse verifyOtp(VerifyOtpRequest request) {
        log.info("verifyOtp request email={}, otp={}", request.getEmail(), request.getOtp());

        var verification = verificationCodeRepository.findByEmailAndCode(
                request.getEmail().trim().toLowerCase(),
                request.getOtp().trim()
        ).orElseThrow(() -> new AppException(ErrorCode.INVALID_OTP));

        // 1 Kiểm tra hết hạn
        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.EXPIRED_OTP);
        }

        // 2 Lấy user tương ứng
        var user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 3 Cập nhật trạng thái xác minh email
        user.setEmailVerified(true);
        userRepository.save(user);

        // 4 Xác định user có password hay chưa
        boolean noPassword = (user.getPassword() == null || user.getPassword().isBlank());

        // 5 Sinh JWT token thật
        String accessToken = generateToken(user);

        // 6 Xóa OTP để tránh reuse
        verificationCodeRepository.delete(verification);

        // (optional) gửi mail chào mừng
        // emailService.sendWelcomeEmail(user.getEmail(), user.getFullName(), "https://flexistudy.vn/home");

        // 7 Trả kết quả cho FE
        return AuthenticationResponse.builder()
                .token(accessToken)
                .authenticated(true)
                .email(user.getEmail())
                .emailVerificationRequired(false)
                .noPassword(noPassword)
                .build();
    }




    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception){
            log.info("Token already expired");
        }
    }


    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("quyhoang.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("userId", user.getId())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }


    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }
}
