package com.junmo.platform.core.mq;

import com.junmo.platform.api.dto.SystemMessage;
import com.junmo.platform.api.dto.UserMessage;
import com.junmo.platform.api.dto.UserStatusMessage;
import com.junmo.platform.core.service.UserService;
import com.junmo.platform.core.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

/**
 * WebSocket消息监听器
 *
 * 功能说明:
 * 1. 监听用户消息
 * 2. 监听广播消息
 * 3. 监听用户状态变更
 * 4. 处理用户订阅
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageListener {

    private final UserService userService;
    private final WebSocketService webSocketService;

    /**
     * 监听用户订阅用户状态
     *
     * @param headerAccessor 消息头访问器
     * @return 用户状态消息
     */
    @SubscribeMapping("/topic/user-status")
    public UserStatusMessage subscribeUserStatus(SimpMessageHeaderAccessor headerAccessor) {
        log.info("用户订阅用户状态: sessionId={}", headerAccessor.getSessionId());

        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        if (userId != null) {
            // 更新用户上线状态
            webSocketService.userOnline(userId);

            // 返回用户状态
            UserStatusMessage message = new UserStatusMessage(userId, "ONLINE");
            return message;
        }

        return new UserStatusMessage(null, "GUEST");
    }

    /**
     * 监听用户订阅系统消息
     *
     * @param headerAccessor 消息头访问器
     * @return 系统消息
     */
    @SubscribeMapping("/topic/system")
    public SystemMessage subscribeSystem(SimpMessageHeaderAccessor headerAccessor) {
        log.info("用户订阅系统消息: sessionId={}", headerAccessor.getSessionId());

        return new SystemMessage("系统通知", "欢迎连接WebSocket服务", "INFO");
    }

    /**
     * 监听用户消息
     *
     * @param message        用户消息
     * @param headerAccessor 消息头访问器
     * @return 用户消息
     */
    @MessageMapping("/app/user/message")
    @SendTo("/topic/user-messages")
    public UserMessage handleUserMessage(@Payload UserMessage message,
                                          SimpMessageHeaderAccessor headerAccessor) {
        log.info("收到用户消息: {}", message);

        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        if (userId != null) {
            message.setSenderId(userId);

            // TODO: 获取用户信息
            // User user = userService.getById(userId);
            // message.setSenderName(user.getUsername());

            // TODO: 保存消息到数据库
            // webSocketService.saveMessage(message);
        }

        return message;
    }

    /**
     * 监听用户状态变更
     *
     * @param message        用户状态消息
     * @param headerAccessor 消息头访问器
     * @return 用户状态消息
     */
    @MessageMapping("/app/user/status")
    @SendTo("/topic/user-status")
    public UserStatusMessage handleUserStatus(@Payload UserStatusMessage message,
                                               SimpMessageHeaderAccessor headerAccessor) {
        log.info("收到用户状态变更: {}", message);

        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        if (userId != null) {
            message.setUserId(userId);

            // TODO: 获取用户信息
            // User user = userService.getById(userId);
            // message.setUsername(user.getUsername());

            // 更新用户状态
            webSocketService.updateUserStatus(userId, message.getStatus());
        }

        return message;
    }

    /**
     * 监听系统广播消息
     *
     * @param message        系统消息
     * @param headerAccessor 消息头访问器
     * @return 系统消息
     */
    @MessageMapping("/app/system/broadcast")
    @SendTo("/topic/system")
    public SystemMessage handleSystemBroadcast(@Payload SystemMessage message,
                                                SimpMessageHeaderAccessor headerAccessor) {
        log.info("收到系统广播消息: {}", message);

        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        if (userId != null) {
            // TODO: 检查用户权限
            // if (!SecurityUtils.hasPermission("system:broadcast")) {
            //     throw new AccessDeniedException("无权限发送系统广播");
            //     }
                    }
            
                    return message;
                }
            
                // TODO: 系统通知消息监听（待实现）
                // @MessageMapping("/app/notification/message")
                // public void handleNotificationMessage(@Payload NotificationMessage message) {
                //     log.info("收到通知消息: {}", message);
                //     // TODO: 实现通知消息处理逻辑
                // }
            }