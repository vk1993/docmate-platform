package com.docmate.user.service;

import com.docmate.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtUtil jwtUtil;

    public String generateToken(String email, UUID userId, String role) {
        return jwtUtil.generateToken(email, userId, role);
    }

    public String generateRefreshToken(String email, UUID userId) {
        return jwtUtil.generateRefreshToken(email, userId);
    }

    public String getEmailFromToken(String token) {
        return jwtUtil.getEmailFromToken(token);
    }

    public String extractUsername(String token) {
        return jwtUtil.getEmailFromToken(token);
    }

    public UUID getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }

    public String getRoleFromToken(String token) {
        return jwtUtil.getRoleFromToken(token);
    }

    public boolean validateToken(String token, String email) {
        return jwtUtil.validateToken(token, email);
    }

    public boolean isRefreshToken(String token) {
        return jwtUtil.isRefreshToken(token);
    }
}
