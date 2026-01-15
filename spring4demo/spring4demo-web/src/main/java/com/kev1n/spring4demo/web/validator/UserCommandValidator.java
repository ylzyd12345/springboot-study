package com.kev1n.spring4demo.web.validator;

import com.kev1n.spring4demo.web.dto.UserCreateRequest;
import com.kev1n.spring4demo.web.dto.UserUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 用户命令验证器
 *
 * <p>负责验证用户创建和更新请求参数的有效性。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@Slf4j
public class UserCommandValidator {

    /**
     * 验证用户创建请求
     *
     * @param request 创建请求参数
     * @return 是否有效
     */
    public boolean validateCreateRequest(UserCreateRequest request) {
        if (request == null) {
            log.warn("创建请求参数为空");
            return false;
        }

        // 验证用户名
        if (!StringUtils.hasText(request.getUsername())) {
            log.warn("用户名不能为空");
            return false;
        }

        if (request.getUsername().length() < 3 || request.getUsername().length() > 50) {
            log.warn("用户名长度必须在3-50之间: length={}", request.getUsername().length());
            return false;
        }

        // 验证邮箱格式
        if (StringUtils.hasText(request.getEmail())) {
            if (!isValidEmail(request.getEmail())) {
                log.warn("邮箱格式无效: email={}", request.getEmail());
                return false;
            }
        }

        // 验证手机号格式
        if (StringUtils.hasText(request.getPhone())) {
            if (!isValidPhone(request.getPhone())) {
                log.warn("手机号格式无效: phone={}", request.getPhone());
                return false;
            }
        }

        // 验证状态
        if (request.getStatus() != null && (request.getStatus() < 0 || request.getStatus() > 1)) {
            log.warn("用户状态无效: status={}", request.getStatus());
            return false;
        }

        return true;
    }

    /**
     * 验证用户更新请求
     *
     * @param request 更新请求参数
     * @return 是否有效
     */
    public boolean validateUpdateRequest(UserUpdateRequest request) {
        if (request == null) {
            log.warn("更新请求参数为空");
            return false;
        }

        // 验证邮箱格式
        if (StringUtils.hasText(request.getEmail())) {
            if (!isValidEmail(request.getEmail())) {
                log.warn("邮箱格式无效: email={}", request.getEmail());
                return false;
            }
        }

        // 验证手机号格式
        if (StringUtils.hasText(request.getPhone())) {
            if (!isValidPhone(request.getPhone())) {
                log.warn("手机号格式无效: phone={}", request.getPhone());
                return false;
            }
        }

        // 验证状态
        if (request.getStatus() != null && (request.getStatus() < 0 || request.getStatus() > 1)) {
            log.warn("用户状态无效: status={}", request.getStatus());
            return false;
        }

        return true;
    }

    /**
     * 验证用户ID
     *
     * @param id 用户ID
     * @return 是否有效
     */
    public boolean validateUserId(Long id) {
        if (id == null || id <= 0) {
            log.warn("用户ID无效: id={}", id);
            return false;
        }
        return true;
    }

    /**
     * 验证用户状态
     *
     * @param status 用户状态
     * @return 是否有效
     */
    public boolean validateStatus(Integer status) {
        if (status == null) {
            log.warn("用户状态不能为空");
            return false;
        }

        if (status < 0 || status > 1) {
            log.warn("用户状态无效: status={}", status);
            return false;
        }

        return true;
    }

    /**
     * 验证邮箱格式
     *
     * @param email 邮箱地址
     * @return 是否有效
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    /**
     * 验证手机号格式
     *
     * @param phone 手机号
     * @return 是否有效
     */
    private boolean isValidPhone(String phone) {
        String phoneRegex = "^1[3-9]\\d{9}$";
        return phone.matches(phoneRegex);
    }
}
