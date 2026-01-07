package com.kev1n.spring4demo.core.service;

import com.kev1n.spring4demo.api.dto.OnlineUser;
import com.kev1n.spring4demo.api.dto.UserMessage;
import com.kev1n.spring4demo.core.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务
 *
 * 功能说明:
 * 1. 管理在线用户
 * 2. 用户上线/下线通知
 * 3. 在线用户列表查询
 * 4. 用户状态管理
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    /**
     * 在线用户缓存 (userId -> OnlineUser)
     */
    private final Map<Long, OnlineUser> onlineUsers = new ConcurrentHashMap<>();

    /**
     * 用户会话缓存 (userId -> sessionId)
     */
    private final Map<Long, String> userSessions = new ConcurrentHashMap<>();

    /**
     * 用户上线
     *
     * @param userId 用户ID
     */
    public void userOnline(Long userId) {
        log.info("用户上线: userId={}", userId);

        User user = userService.getById(userId);
        if (user != null) {
            OnlineUser onlineUser = new OnlineUser();
            onlineUser.setUserId(user.getId());
            onlineUser.setUsername(user.getUsername());
            onlineUser.setRealName(user.getRealName());
            onlineUser.setAvatar(user.getAvatar());
            onlineUser.setStatus("ONLINE");
            onlineUser.setOnlineTime(LocalDateTime.now());
            onlineUser.setLastActiveTime(LocalDateTime.now());

            onlineUsers.put(userId, onlineUser);

            // 广播用户上线消息
            com.kev1n.spring4demo.api.dto.UserStatusMessage message =
                    new com.kev1n.spring4demo.api.dto.UserStatusMessage(
                            userId, user.getUsername(), "ONLINE"
                    );
            messagingTemplate.convertAndSend("/topic/user-status", message);

            log.info("用户上线成功: userId={}, username={}", userId, user.getUsername());
        }
    }

    /**
     * 用户下线
     *
     * @param userId 用户ID
     */
    public void userOffline(Long userId) {
        log.info("用户下线: userId={}", userId);

        OnlineUser onlineUser = onlineUsers.remove(userId);
        if (onlineUser != null) {
            // 广播用户下线消息
            com.kev1n.spring4demo.api.dto.UserStatusMessage message =
                    new com.kev1n.spring4demo.api.dto.UserStatusMessage(
                            userId, onlineUser.getUsername(), "OFFLINE"
                    );
            messagingTemplate.convertAndSend("/topic/user-status", message);

            log.info("用户下线成功: userId={}, username={}", userId, onlineUser.getUsername());
        }
    }

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     */
    public void updateUserStatus(Long userId, String status) {
        log.info("更新用户状态: userId={}, status={}", userId, status);

        OnlineUser onlineUser = onlineUsers.get(userId);
        if (onlineUser != null) {
            onlineUser.setStatus(status);
            onlineUser.setLastActiveTime(LocalDateTime.now());

            // 广播用户状态更新
            com.kev1n.spring4demo.api.dto.UserStatusMessage message =
                    new com.kev1n.spring4demo.api.dto.UserStatusMessage(
                            userId, onlineUser.getUsername(), status
                    );
            messagingTemplate.convertAndSend("/topic/user-status", message);

            log.info("用户状态更新成功: userId={}, status={}", userId, status);
        }
    }

    /**
     * 获取在线用户列表
     *
     * @return 在线用户列表
     */
    public List<OnlineUser> getOnlineUsers() {
        log.info("获取在线用户列表: count={}", onlineUsers.size());
        return new ArrayList<>(onlineUsers.values());
    }

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        return onlineUsers.size();
    }

    /**
     * 判断用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(Long userId) {
        return onlineUsers.containsKey(userId);
    }

    /**
     * 获取在线用户
     *
     * @param userId 用户ID
     * @return 在线用户信息
     */
    public OnlineUser getOnlineUser(Long userId) {
        return onlineUsers.get(userId);
    }

    /**
     * 保存用户会话
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     */
    public void saveUserSession(Long userId, String sessionId) {
        userSessions.put(userId, sessionId);
        log.info("保存用户会话: userId={}, sessionId={}", userId, sessionId);
    }

    /**
     * 移除用户会话
     *
     * @param userId 用户ID
     */
    public void removeUserSession(Long userId) {
        userSessions.remove(userId);
        log.info("移除用户会话: userId={}", userId);
    }

    /**
     * 发送用户消息
     *
     * @param userId  用户ID
     * @param message 消息
     */
    public void sendUserMessage(Long userId, UserMessage message) {
        log.info("发送用户消息: userId={}, message={}", userId, message);
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/messages", message);
    }

    /**
     * 广播系统消息
     *
     * @param message 消息
     */
    public void broadcastSystemMessage(com.kev1n.spring4demo.api.dto.SystemMessage message) {
        log.info("广播系统消息: message={}", message);
        messagingTemplate.convertAndSend("/topic/system", message);
    }

    // TODO: 保存消息到数据库（待实现）
    // public void saveMessage(UserMessage message) {
    //     log.info("保存消息到数据库: message={}", message);
    //     // TODO: 实现消息持久化逻辑
    // }

    // TODO: 发送订单消息（待实现）
    // public void sendOrderMessage(Long userId, OrderMessage message) {
    //     log.info("发送订单消息: userId={}, message={}", userId, message);
    //     // TODO: 实现订单消息发送逻辑
    // }

    // TODO: 发送系统通知（待实现）
    // public void sendNotification(Long userId, NotificationMessage message) {
    //     log.info("发送系统通知: userId={}, message={}", userId, message);
    //     // TODO: 实现系统通知发送逻辑
    // }
}