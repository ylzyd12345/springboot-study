package com.kev1n.spring4demo.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kev1n.spring4demo.common.constant.ApiConstants;
import com.kev1n.spring4demo.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT认证入口点
 * 处理未认证的请求
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, 
                       HttpServletResponse response,
                       AuthenticationException authException) throws IOException {
        
        log.error("Responding with unauthorized error. Message - {}", authException.getMessage());
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());
        body.put("timestamp", LocalDateTime.now());
        body.put("code", ApiConstants.CODE_UNAUTHORIZED);
        
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}