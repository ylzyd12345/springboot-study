package com.kev1n.spring4demo.web.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.kev1n.spring4demo.common.enums.ErrorCode;
import com.kev1n.spring4demo.common.exception.BusinessException;
import com.kev1n.spring4demo.common.exception.SystemException;
import com.kev1n.spring4demo.common.exception.TokenInvalidException;
import com.kev1n.spring4demo.common.exception.ValidationException;
import com.kev1n.spring4demo.web.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理系统中的各种异常，返回标准的API响应格式
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // ==================== Sa-Token 认证异常 ====================
    
    /**
     * 处理 Sa-Token 未登录异常
     * 
     * @param e 未登录异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<ApiResponse<?>> handleNotLoginException(
            NotLoginException e, HttpServletRequest request) {
        log.warn("未登录异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        String message = switch (e.getType()) {
            case NotLoginException.NOT_TOKEN -> "未提供Token";
            case NotLoginException.INVALID_TOKEN -> "Token无效";
            case NotLoginException.TOKEN_TIMEOUT -> "Token已过期，请重新登录";
            case NotLoginException.BE_REPLACED -> "账号已在其他设备登录，如非本人操作请立即修改密码";
            case NotLoginException.KICK_OUT -> "账号已被踢下线";
            case NotLoginException.TOKEN_FREEZE -> "Token已被冻结";
            case NotLoginException.NO_PREFIX -> "Token前缀错误";
            default -> "未登录";
        };
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.NOT_LOGIN.getCode(), message));
    }
    
    /**
     * 处理 Sa-Token 无权限异常
     * 
     * @param e 无权限异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(NotPermissionException.class)
    public ResponseEntity<ApiResponse<?>> handleNotPermissionException(
            NotPermissionException e, HttpServletRequest request) {
        log.warn("无权限异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ErrorCode.NO_PERMISSION.getCode(), "无权限访问: " + e.getPermission()));
    }
    
    /**
     * 处理 Sa-Token 无角色异常
     * 
     * @param e 无角色异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(NotRoleException.class)
    public ResponseEntity<ApiResponse<?>> handleNotRoleException(
            NotRoleException e, HttpServletRequest request) {
        log.warn("无角色异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ErrorCode.NO_ROLE.getCode(), "无该角色: " + e.getRole()));
    }
    
    // ==================== 自定义认证异常 ====================
    
    /**
     * 处理自定义未登录异常
     * 
     * @param e 未登录异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(com.kev1n.spring4demo.common.exception.NotLoginException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomNotLoginException(
            com.kev1n.spring4demo.common.exception.NotLoginException e, HttpServletRequest request) {
        log.warn("未登录异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.NOT_LOGIN.getCode(), e.getMessage()));
    }
    
    /**
     * 处理自定义无权限异常
     * 
     * @param e 无权限异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(com.kev1n.spring4demo.common.exception.NotPermissionException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomNotPermissionException(
            com.kev1n.spring4demo.common.exception.NotPermissionException e, HttpServletRequest request) {
        log.warn("无权限异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ErrorCode.NO_PERMISSION.getCode(), e.getMessage()));
    }
    
    /**
     * 处理Token无效异常
     * 
     * @param e Token无效异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(TokenInvalidException.class)
    public ResponseEntity<ApiResponse<?>> handleTokenInvalidException(
            TokenInvalidException e, HttpServletRequest request) {
        log.warn("Token无效异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ErrorCode.INVALID_TOKEN.getCode(), e.getMessage()));
    }
    
    // ==================== 业务异常 ====================
    
    /**
     * 处理业务异常
     * 
     * @param e 业务异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(
            BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        return ResponseEntity.status(e.getHttpStatus())
                .body(ApiResponse.error(e.getHttpStatus(), e.getMessage()));
    }
    
    // ==================== 系统异常 ====================
    
    /**
     * 处理系统异常
     * 
     * @param e 系统异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ApiResponse<?>> handleSystemException(
            SystemException e, HttpServletRequest request) {
        log.error("系统异常: {}, URI: {}", e.getMessage(), request.getRequestURI(), e);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统错误，请稍后重试"));
    }
    
    // ==================== 参数校验异常 ====================
    
    /**
     * 处理参数校验异常
     * 
     * @param e 参数校验异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            ValidationException e, HttpServletRequest request) {
        log.warn("参数校验异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR.getCode(), e.getMessage()));
    }
    
    /**
     * 处理方法参数校验异常（@Valid）
     * 
     * @param e 方法参数校验异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("方法参数校验异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR.getCode(), "参数校验失败", errors));
    }
    
    /**
     * 处理绑定异常
     * 
     * @param e 绑定异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<?>> handleBindException(
            BindException e, HttpServletRequest request) {
        log.warn("绑定异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        List<String> errors = e.getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "参数绑定失败", errors));
    }
    
    /**
     * 处理约束违反异常
     * 
     * @param e 约束违反异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolationException(
            ConstraintViolationException e, HttpServletRequest request) {
        log.warn("约束违反异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        List<String> errors = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR.getCode(), "参数校验失败", errors));
    }
    
    /**
     * 处理缺少请求参数异常
     * 
     * @param e 缺少请求参数异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("缺少请求参数异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "缺少请求参数: " + e.getParameterName()));
    }
    
    /**
     * 处理方法参数类型不匹配异常
     * 
     * @param e 方法参数类型不匹配异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("方法参数类型不匹配异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "参数类型错误: " + e.getName()));
    }
    
    // ==================== HTTP相关异常 ====================
    
    /**
     * 处理请求方法不支持异常
     * 
     * @param e 请求方法不支持异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("请求方法不支持异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(ErrorCode.METHOD_NOT_ALLOWED.getCode(), "请求方法不支持: " + e.getMethod()));
    }
    
    /**
     * 处理404异常
     * 
     * @param e 404异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoHandlerFoundException(
            NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("404异常: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ErrorCode.RESOURCE_NOT_FOUND.getCode(), "请求的资源不存在"));
    }
    
    /**
     * 处理静态资源未找到异常
     * 
     * @param e 静态资源未找到异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoResourceFoundException(
            NoResourceFoundException e, HttpServletRequest request) {
        log.warn("静态资源未找到: {}, URI: {}", e.getMessage(), request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ErrorCode.RESOURCE_NOT_FOUND.getCode(), "请求的资源不存在"));
    }
    
    // ==================== 通用异常 ====================
    
    /**
     * 处理所有未捕获的异常
     * 
     * @param e 异常
     * @param request HTTP请求
     * @return API响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(
            Exception e, HttpServletRequest request) {
        log.error("未捕获的异常: {}, URI: {}", e.getMessage(), request.getRequestURI(), e);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统错误，请稍后重试"));
    }
}