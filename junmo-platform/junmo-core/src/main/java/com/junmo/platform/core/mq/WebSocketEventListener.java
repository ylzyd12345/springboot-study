package com.junmo.platform.core.mq;

import com.junmo.platform.core.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

/**
 * WebSocket事件监听器
 *
 * 功能说明:
 * 1. 监听WebSocket连接建立事件
 * 2. 监听WebSocket断开连接事件
 * 3. 监听WebSocket订阅事件
 * 4. 监听WebSocket取消订阅事件
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final WebSocketService webSocketService;

    /**
     * 监听WebSocket连接建立事件
     *
     * @param event 连接事件
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        log.info("WebSocket连接建立: sessionId={}", sessionId);

        // TODO: 从session attributes中获取userId
        // Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        // if (userId != null) {
        //     webSocketService.saveUserSession(userId, sessionId);
        //     webSocketService.userOnline(userId);
        // }
    }

    /**
     * 监听WebSocket断开连接事件
     *
     * @param event 断开连接事件
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        log.info("WebSocket断开连接: sessionId={}", sessionId);

        // TODO: 从session attributes中获取userId
        // Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        // if (userId != null) {
        //     webSocketService.removeUserSession(userId);
        //     webSocketService.userOffline(userId);
        // }
    }

    /**
     * 监听WebSocket订阅事件
     *
     * @param event 订阅事件
     */
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();

        log.info("WebSocket订阅: sessionId={}, destination={}", sessionId, destination);

        // TODO: 记录用户订阅信息
        // Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        // if (userId != null && destination != null) {
        //     webSocketService.saveUserSubscription(userId, sessionId, destination);
        // }
    }

    /**
     * 监听WebSocket取消订阅事件
     *
     * @param event 取消订阅事件
     */
    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String subscriptionId = headerAccessor.getSubscriptionId();

        log.info("WebSocket取消订阅: sessionId={}, subscriptionId={}", sessionId, subscriptionId);

        // TODO: 移除用户订阅信息
        // webSocketService.removeUserSubscription(sessionId, subscriptionId);
    }
}