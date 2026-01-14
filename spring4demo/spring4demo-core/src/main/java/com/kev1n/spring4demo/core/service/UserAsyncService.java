package com.kev1n.spring4demo.core.service;

import com.kev1n.spring4demo.common.exception.BusinessException;
import com.kev1n.spring4demo.core.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 用户异步服务
 * 提供用户相关的异步处理能力，如邮件发送、验证码发送、日志记录、通知发送等
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAsyncService {

    private final UserService userService;
    private final UserLogService userLogService;

    // ==================== 邮件相关异步方法 ====================

    /**
     * 异步发送欢迎邮件
     * 在用户注册成功后，异步发送欢迎邮件给用户
     *
     * @param userId 用户ID
     * @return CompletableFuture<Void>
     */
    @Async("taskExecutor")
    public CompletableFuture<Void> sendWelcomeEmailAsync(Long userId) {
        log.info("开始异步发送欢迎邮件: userId={}", userId);
        try {
            User user = userService.getById(userId);
            if (user != null) {
                // TODO: 待实现邮件服务后启用
                // emailService.sendWelcomeEmail(user);
                log.info("欢迎邮件发送成功（模拟）: userId={}, email={}", userId, user.getEmail());
                // 记录日志
                userLogService.logUserAction(
                        userId,
                        user.getUsername(),
                        "SEND_WELCOME_EMAIL",
                        String.format("发送欢迎邮件到: %s", user.getEmail()),
                        null,
                        null
                );
            } else {
                log.warn("用户不存在，无法发送欢迎邮件: userId={}", userId);
            }
        } catch (BusinessException e) {
            log.error("发送欢迎邮件失败(业务异常): userId={}, error={}", userId, e.getMessage(), e);
            // 异步操作失败不影响主流程，记录日志即可
        } catch (RuntimeException e) {
            log.error("发送欢迎邮件失败(运行时异常): userId={}, error={}", userId, e.getMessage(), e);
            // 异步操作失败不影响主流程，记录日志即可
        } catch (Exception e) {
            log.error("发送欢迎邮件失败(未知异常): userId={}", userId, e);
            // 异步操作失败不影响主流程，记录日志即可
        }
        log.info("异步发送欢迎邮件完成: userId={}", userId);
        return CompletableFuture.completedFuture(null);
    }

    // ==================== 验证码相关异步方法 ====================

    /**
     * 异步发送验证码
     * 发送验证码（邮件或短信）用于用户验证
     *
     * @param userId 用户ID
     * @param code   验证码
     * @param type   验证码类型（EMAIL/SMS）
     * @return CompletableFuture<Void>
     */
    @Async("taskExecutor")
    public CompletableFuture<Void> sendVerificationCodeAsync(Long userId, String code, String type) {
        log.info("开始异步发送验证码: userId={}, code={}, type={}", userId, code, type);
        try {
            User user = userService.getById(userId);
            if (user != null) {
                // TODO: 待实现邮件/短信服务后启用
                if ("EMAIL".equalsIgnoreCase(type)) {
                    // emailService.sendVerificationCode(user, code);
                    log.info("验证码邮件发送成功（模拟）: userId={}, email={}, code={}", userId, user.getEmail(), code);
                } else if ("SMS".equalsIgnoreCase(type)) {
                    // smsService.sendVerificationCode(user, code);
                    log.info("验证码短信发送成功（模拟）: userId={}, phone={}, code={}", userId, user.getPhone(), code);
                }
                // 记录日志
                userLogService.logUserAction(
                        userId,
                        user.getUsername(),
                        "SEND_VERIFICATION_CODE",
                        String.format("发送验证码: type=%s", type),
                        null,
                        null
                );
            } else {
                log.warn("用户不存在，无法发送验证码: userId={}", userId);
            }
        } catch (BusinessException e) {
            log.error("发送验证码失败(业务异常): userId={}, type={}, error={}", userId, type, e.getMessage(), e);
            // 异步操作失败不影响主流程，记录日志即可
        } catch (IllegalArgumentException e) {
            log.error("发送验证码失败(参数异常): userId={}, type={}, error={}", userId, type, e.getMessage(), e);
            // 异步操作失败不影响主流程，记录日志即可
        } catch (RuntimeException e) {
            log.error("发送验证码失败(运行时异常): userId={}, type={}, error={}", userId, type, e.getMessage(), e);
            // 异步操作失败不影响主流程，记录日志即可
        } catch (Exception e) {
            log.error("发送验证码失败(未知异常): userId={}, type={}", userId, type, e);
            // 异步操作失败不影响主流程，记录日志即可
        }
        log.info("异步发送验证码完成: userId={}", userId);
        return CompletableFuture.completedFuture(null);
    }

    // ==================== 日志记录相关异步方法 ====================

    /**
     * 异步记录用户操作日志
     * 在用户执行操作后，异步记录操作日志到数据库
     *
     * @param userId    用户ID
     * @param username  用户名
     * @param action    操作类型（CREATE, UPDATE, DELETE, LOGIN等）
     * @param details   操作详情（JSON格式）
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @return CompletableFuture<Void>
     */
    @Async("taskExecutor")
    public CompletableFuture<Void> logUserActionAsync(Long userId, String username, String action,
                                                     String details, String ipAddress, String userAgent) {
        log.debug("开始异步记录用户操作日志: userId={}, action={}", userId, action);
        try {
            userLogService.logUserAction(userId, username, action, details, ipAddress, userAgent);
            log.debug("异步记录用户操作日志成功: userId={}, action={}", userId, action);
        } catch (BusinessException e) {
            log.error("记录用户操作日志失败(业务异常): userId={}, action={}, error={}", userId, action, e.getMessage(), e);
            // 异步操作失败不影响主流程，记录日志即可
        } catch (RuntimeException e) {
            log.error("记录用户操作日志失败(运行时异常): userId={}, action={}, error={}", userId, action, e.getMessage(), e);
            // 异步操作失败不影响主流程，记录日志即可
        } catch (Exception e) {
            log.error("记录用户操作日志失败(未知异常): userId={}, action={}", userId, action, e);
            // 异步操作失败不影响主流程，记录日志即可
        }
        return CompletableFuture.completedFuture(null);
    }

    // ==================== 通知相关异步方法 ====================

    /**
     * 异步发送通知
     * 发送系统通知给用户
     *
     * @param userId  用户ID
     * @param title   通知标题
     * @param content 通知内容
     * @return CompletableFuture<Void>
     */
    @Async("taskExecutor")
    public CompletableFuture<Void> sendNotificationAsync(Long userId, String title, String content) {
        log.info("开始异步发送通知: userId={}, title={}", userId, title);
        try {
            User user = userService.getById(userId);
            if (user != null) {
                // TODO: 待实现通知服务后启用
                // notificationService.sendNotification(user, title, content);
                log.info("通知发送成功（模拟）: userId={}, title={}, content={}", userId, title, content);
                // 记录日志
                userLogService.logUserAction(
                        userId,
                        user.getUsername(),
                        "SEND_NOTIFICATION",
                        String.format("发送通知: title=%s", title),
                        null,
                        null
                );
            } else {
                log.warn("用户不存在，无法发送通知: userId={}", userId);
            }
        } catch (BusinessException e) {
            log.error("发送通知失败(业务异常): userId={}, title={}, error={}", userId, title, e.getMessage(), e);
            // 异步操作失败不影响主流程，记录日志即可
        } catch (RuntimeException e) {
            log.error("发送通知失败(运行时异常): userId={}, title={}, error={}", userId, title, e.getMessage(), e);
            // 异步操作失败不影响主流程，记录日志即可
        } catch (Exception e) {
            log.error("发送通知失败(未知异常): userId={}, title={}", userId, title, e);
            // 异步操作失败不影响主流程，记录日志即可
        }
        log.info("异步发送通知完成: userId={}", userId);
        return CompletableFuture.completedFuture(null);
    }

    // ==================== 批量处理相关异步方法 ====================

    /**
     * 异步批量发送欢迎邮件
     * 批量发送欢迎邮件给多个用户
     *
     * @param userIds 用户ID列表
     * @return CompletableFuture<Integer> 返回成功发送的数量
     */
    @Async("taskExecutor")
    public CompletableFuture<Integer> batchSendWelcomeEmailAsync(java.util.List<Long> userIds) {
        log.info("开始异步批量发送欢迎邮件: count={}", userIds.size());
        int successCount = 0;
        for (Long userId : userIds) {
            try {
                sendWelcomeEmailAsync(userId).get();
                successCount++;
            } catch (ExecutionException e) {
                log.error("批量发送欢迎邮件失败(执行异常): userId={}, error={}", userId, e.getMessage(), e);
                // 批量操作中单个失败不影响其他用户，继续处理
            } catch (InterruptedException e) {
                log.error("批量发送欢迎邮件失败(中断异常): userId={}", userId, e);
                Thread.currentThread().interrupt(); // 恢复中断状态
            } catch (Exception e) {
                log.error("批量发送欢迎邮件失败(未知异常): userId={}", userId, e);
                // 批量操作中单个失败不影响其他用户，继续处理
            }
        }
        log.info("异步批量发送欢迎邮件完成: total={}, success={}", userIds.size(), successCount);
        return CompletableFuture.completedFuture(successCount);
    }

    }