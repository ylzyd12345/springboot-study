package com.kev1n.spring4demo.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举。
 * 统一管理系统错误码。
 *
 * <p>错误码格式：{HTTP状态码}{业务模块}{具体错误}</p>
 * <p>例如：40101 表示 401（认证失败）+ 0（认证模块）+ 1（具体错误）</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ==================== 成功 ====================
    /** 操作成功 */
    SUCCESS(200, "操作成功"),

    // ==================== 认证相关 40xxx ====================
    /** 未登录 */
    NOT_LOGIN(40100, "未登录"),
    /** 未提供Token */
    NOT_TOKEN(40101, "未提供Token"),
    /** Token无效 */
    INVALID_TOKEN(40102, "Token无效"),
    /** Token已过期 */
    TOKEN_TIMEOUT(40103, "Token已过期"),
    /** 账号已在其他设备登录 */
    TOKEN_REPLACED(40104, "账号已在其他设备登录"),
    /** 账号已被踢下线 */
    TOKEN_KICK_OUT(40105, "账号已被踢下线"),
    /** Token已被冻结 */
    TOKEN_FREEZE(40106, "Token已被冻结"),
    /** Token前缀错误 */
    NO_PREFIX(40107, "Token前缀错误"),

    // ==================== 权限相关 40xxx ====================
    /** 无权限访问 */
    NO_PERMISSION(40300, "无权限访问"),
    /** 无该角色 */
    NO_ROLE(40301, "无该角色"),

    // ==================== 请求错误 40xxx ====================
    /** 请求参数错误 */
    BAD_REQUEST(40000, "请求参数错误"),
    /** 参数校验失败 */
    VALIDATION_ERROR(40001, "参数校验失败"),
    /** 资源不存在 */
    RESOURCE_NOT_FOUND(40002, "资源不存在"),
    /** 请求方法不支持 */
    METHOD_NOT_ALLOWED(40003, "请求方法不支持"),
    /** 不支持的媒体类型 */
    UNSUPPORTED_MEDIA_TYPE(40004, "不支持的媒体类型"),

    // ==================== 业务错误 42xxx ====================
    /** 业务处理失败 */
    BUSINESS_ERROR(42000, "业务处理失败"),
    /** 用户不存在 */
    USER_NOT_FOUND(42001, "用户不存在"),
    /** 用户已存在 */
    USER_ALREADY_EXISTS(42002, "用户已存在"),
    /** 用户已被禁用 */
    USER_DISABLED(42003, "用户已被禁用"),
    /** 密码错误 */
    PASSWORD_ERROR(42004, "密码错误"),
    /** 原密码错误 */
    OLD_PASSWORD_ERROR(42005, "原密码错误"),
    /** 数据冲突 */
    DATA_CONFLICT(42006, "数据冲突"),
    /** 操作失败 */
    OPERATION_FAILED(42007, "操作失败"),

    // ==================== 系统错误 50xxx ====================
    /** 系统错误 */
    SYSTEM_ERROR(50000, "系统错误"),
    /** 数据库错误 */
    DATABASE_ERROR(50001, "数据库错误"),
    /** 网络错误 */
    NETWORK_ERROR(50002, "网络错误"),
    /** 外部服务错误 */
    EXTERNAL_SERVICE_ERROR(50003, "外部服务错误"),
    /** 文件上传失败 */
    FILE_UPLOAD_ERROR(50004, "文件上传失败"),
    /** 文件下载失败 */
    FILE_DOWNLOAD_ERROR(50005, "文件下载失败"),

    // ==================== 服务不可用 50xxx ====================
    /** 服务暂时不可用 */
    SERVICE_UNAVAILABLE(50300, "服务暂时不可用"),
    /** 网关超时 */
    GATEWAY_TIMEOUT(50400, "网关超时");
    
    /** 错误码 */
    private final int code;
    
    /** 错误消息 */
    private final String message;
    
    /**
     * 根据错误码获取枚举。
     *
     * @param code 错误码
     * @return 错误码枚举
     */
    public static ErrorCode fromCode(int code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return SYSTEM_ERROR;
    }

    /**
     * 获取HTTP状态码。
     *
     * @return HTTP状态码
     */
    public int getHttpStatus() {
        return code / 1000;
    }
}