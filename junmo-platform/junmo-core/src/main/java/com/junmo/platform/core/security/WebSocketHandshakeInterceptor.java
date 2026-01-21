package com.junmo.platform.core.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket握手拦截器
 *
 * 功能说明:
 * 1. 验证用户身份
 * 2. 提取用户信息
 * 3. 记录连接日志
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Component
@Slf4j
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * 握手前处理
     *
     * @param request    HTTP请求
     * @param response   HTTP响应
     * @param wsHandler  WebSocket处理器
     * @param attributes 属性集合
     * @return 是否允许握手
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;

            // 从请求中获取用户ID
            String userId = servletRequest.getServletRequest().getParameter("userId");
            String token = servletRequest.getServletRequest().getParameter("token");

            log.info("WebSocket握手请求: userId={}, token={}", userId, token);

            // TODO: 实现token验证逻辑
            // if (StringUtils.isEmpty(token) || !tokenService.validateToken(token)) {
            //     log.warn("WebSocket握手失败: 无效的token");
            //     return false;
            // }

            // 将用户信息存入attributes
            if (userId != null) {
                attributes.put("userId", Long.parseLong(userId));
            }

            return true;
        }

        log.warn("WebSocket握手失败: 无效的请求类型");
        return false;
    }

    /**
     * 握手后处理
     *
     * @param request   HTTP请求
     * @param response  HTTP响应
     * @param wsHandler WebSocket处理器
     * @param exception 异常
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket握手异常", exception);
        } else {
            log.info("WebSocket握手成功");
        }
    }
}