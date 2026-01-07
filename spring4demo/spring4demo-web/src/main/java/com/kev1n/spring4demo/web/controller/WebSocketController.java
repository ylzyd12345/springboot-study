package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.api.dto.OnlineUser;
import com.kev1n.spring4demo.api.dto.SystemMessage;
import com.kev1n.spring4demo.api.dto.UserMessage;
import com.kev1n.spring4demo.api.dto.UserStatusMessage;
import com.kev1n.spring4demo.core.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * WebSocket控制器
 *
 * 功能说明:
 * 1. 发送用户消息
 * 2. 广播系统消息
 * 3. 发送给特定用户消息
 * 4. 查询在线用户列表
 *
 * @author spring4demo
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/websocket")
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketService webSocketService;

    /**
     * 发送用户状态更新消息
     *
     * @param userId 用户ID
     * @param status 状态
     */
    @MessageMapping("/app/user/status")
    @SendTo("/topic/user-status")
    public UserStatusMessage sendUserStatusUpdate(Long userId, String status) {
        log.info("发送用户状态更新: userId={}, status={}", userId, status);

        UserStatusMessage message = new UserStatusMessage(userId, status);
        messagingTemplate.convertAndSend("/topic/user-status", message);

        // 更新在线用户状态
        webSocketService.updateUserStatus(userId, status);

        return message;
    }

    /**
     * 发送用户专属消息
     *
     * @param userId  用户ID
     * @param content 消息内容
     */
    @MessageMapping("/app/user/message")
    public void sendUserPrivateMessage(Long userId, String content) {
        log.info("发送用户专属消息: userId={}, content={}", userId, content);

        UserMessage message = new UserMessage(userId, content);
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/messages",
                message
        );

        // TODO: 保存消息到数据库
        // webSocketService.saveMessage(message);
    }

    /**
     * 广播系统消息
     *
     * @param content 消息内容
     */
    @MessageMapping("/app/system/broadcast")
    @SendTo("/topic/system")
    public SystemMessage broadcastSystemMessage(String content) {
        log.info("广播系统消息: {}", content);

        SystemMessage message = new SystemMessage("系统通知", content, "INFO");
        messagingTemplate.convertAndSend("/topic/system", message);

        return message;
    }

    /**
     * 发送用户消息（REST API）
     *
     * @param receiverId 接收者ID
     * @param content    消息内容
     * @return 发送结果
     */
    @GetMapping("/send-message")
    public ResponseEntity<String> sendUserMessage(Long senderId, Long receiverId, String content) {
        log.info("发送用户消息: senderId={}, receiverId={}, content={}", senderId, receiverId, content);

        // TODO: 从当前登录用户获取senderId
        // Long senderId = SecurityUtils.getCurrentUserId();

        UserMessage message = new UserMessage(senderId, null, receiverId, content);
        messagingTemplate.convertAndSendToUser(
                receiverId.toString(),
                "/queue/messages",
                message
        );

        // TODO: 保存消息到数据库
        // webSocketService.saveMessage(message);

        return ResponseEntity.ok("消息发送成功");
    }

    /**
     * 广播消息（REST API）
     *
     * @param content 消息内容
     * @return 发送结果
     */
    @GetMapping("/broadcast")
    public ResponseEntity<String> broadcast(String content) {
        log.info("广播消息: {}", content);

        SystemMessage message = new SystemMessage("系统广播", content, "INFO");
        messagingTemplate.convertAndSend("/topic/system", message);

        return ResponseEntity.ok("广播消息发送成功");
    }

    /**
     * 查询在线用户列表
     *
     * @return 在线用户列表
     */
    @GetMapping("/online-users")
    public ResponseEntity<List<OnlineUser>> getOnlineUsers() {
        log.info("查询在线用户列表");

        List<OnlineUser> onlineUsers = webSocketService.getOnlineUsers();
        return ResponseEntity.ok(onlineUsers);
    }

    /**
     * 用户上线
     *
     * @param userId 用户ID
     * @return 上线结果
     */
    @GetMapping("/online")
    public ResponseEntity<String> userOnline(Long userId) {
        log.info("用户上线: userId={}", userId);

        // TODO: 从当前登录用户获取userId
        // Long userId = SecurityUtils.getCurrentUserId();

        webSocketService.userOnline(userId);
        return ResponseEntity.ok("用户上线成功");
    }

    /**
     * 用户下线
     *
     * @param userId 用户ID
     * @return 下线结果
     */
    @GetMapping("/offline")
    public ResponseEntity<String> userOffline(Long userId) {
        log.info("用户下线: userId={}", userId);

        // TODO: 从当前登录用户获取userId
        // Long userId = SecurityUtils.getCurrentUserId();

        webSocketService.userOffline(userId);
        return ResponseEntity.ok("用户下线成功");
    }
}