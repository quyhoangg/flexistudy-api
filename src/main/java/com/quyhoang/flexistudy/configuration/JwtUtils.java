package com.quyhoang.flexistudy.configuration;

import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtils {
    private final CustomJwtDecoder jwtDecoder;

    public String getUserIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String token = header.substring(7);
        Jwt jwt = jwtDecoder.decode(token); // Dùng lại class bạn đã có

        return jwt.getClaim("userId");
    }
}

