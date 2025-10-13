package com.quyhoang.flexistudy.configuration;

import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class SecurityUtils {

    // Trích xuất userId từ header Authorization
    public String extractUserIdFromToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;

            String token = authHeader.substring(7);
            SignedJWT signedJWT = SignedJWT.parse(token);

            Object claim = signedJWT.getJWTClaimsSet().getClaim("userId");
            return claim != null ? claim.toString() : null;
        } catch (ParseException e) {
            return null;
        }
    }
}
