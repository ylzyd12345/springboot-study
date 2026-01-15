package com.kev1n.spring4demo.web.validator;

import com.kev1n.spring4demo.web.dto.UserQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 用户查询验证器
 *
 * <p>负责验证用户查询请求参数的有效性。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
@Slf4j
public class UserQueryValidator {

    /**
     * 验证用户查询请求
     *
     * @param request 查询请求参数
     * @return 是否有效
     */
    public boolean validate(UserQueryRequest request) {
        if (request == null) {
            log.warn("查询请求参数为空");
            return false;
        }

        // 验证分页参数
        if (request.getCurrent() != null && request.getCurrent() < 0) {
            log.warn("当前页码不能小于0: current={}", request.getCurrent());
            return false;
        }

        if (request.getSize() != null) {
            if (request.getSize() < 1) {
                log.warn("每页大小不能小于1: size={}", request.getSize());
                return false;
            }
            if (request.getSize() > 1000) {
                log.warn("每页大小不能超过1000: size={}", request.getSize());
                return false;
            }
        }

        // 验证状态参数
        if (request.getStatus() != null && (request.getStatus() < 0 || request.getStatus() > 1)) {
            log.warn("用户状态参数无效: status={}", request.getStatus());
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
     * 验证关键字搜索参数
     *
     * @param keyword 关键字
     * @return 是否有效
     */
    public boolean validateKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return false;
        }

        if (keyword.length() > 100) {
            log.warn("关键字长度不能超过100: length={}", keyword.length());
            return false;
        }

        return true;
    }
}