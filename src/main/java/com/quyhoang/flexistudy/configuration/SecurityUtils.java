package com.quyhoang.flexistudy.configuration;

import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class SecurityUtils {

    // Trích xuất userId từ header Authorization
    public String extractUserIdFromToken(HttpServletRequest request) {
        if (request == null) return null;

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7); // remove "Bearer "
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            Object claim = signedJWT.getJWTClaimsSet().getClaim("userId");
            return claim != null ? claim.toString() : null;
        } catch (ParseException e) {
            // Optional: log error if you want to debug malformed tokens
            // log.warn("Invalid JWT token: {}", e.getMessage());
            return null;
        }
    }
}
