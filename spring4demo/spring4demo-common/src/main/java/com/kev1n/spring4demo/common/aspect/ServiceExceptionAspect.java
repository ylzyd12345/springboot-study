package com.kev1n.spring4demo.common.aspect;

import com.kev1n.spring4demo.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

/**
 * Service层异常处理切面
 *
 * <p>统一处理Service层异常，自动记录日志并转换为BusinessException。</p>
 * <p>注意：此切面必须在事务切面之后执行，确保事务正常回滚。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Aspect
@Component
@Order(100) // 确保在事务切面之后执行（事务切面Order通常为Integer.MIN_VALUE）
public class ServiceExceptionAspect {

    @Around("execution(* com.kev1n.spring4demo.core.service..*.*(..))")
    public Object handleServiceException(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String operation = String.format("%s.%s", className, methodName);
        
        try {
            return joinPoint.proceed();
        } catch (DuplicateKeyException e) {
            log.error("[{}] 数据重复异常: error={}", operation, e.getMessage(), e);
            // 重新抛出BusinessException，确保事务回滚
            throw new BusinessException("数据已存在");
        } catch (DataIntegrityViolationException e) {
            log.error("[{}] 数据完整性异常: error={}", operation, e.getMessage(), e);
            // 重新抛出BusinessException，确保事务回滚
            throw new BusinessException("数据完整性约束违反");
        } catch (DataAccessException e) {
            log.error("[{}] 数据库访问异常: error={}", operation, e.getMessage(), e);
            // 重新抛出BusinessException，确保事务回滚
            throw new BusinessException("系统繁忙，请稍后重试");
        } catch (BusinessException e) {
            log.error("[{}] 业务异常: error={}", operation, e.getMessage(), e);
            // 重新抛出BusinessException，确保事务回滚
            throw e;
        } catch (RuntimeException e) {
            log.error("[{}] 运行时异常: error={}", operation, e.getMessage(), e);
            // 重新抛出BusinessException，确保事务回滚
            throw new BusinessException("系统异常，操作失败");
        } catch (Exception e) {
            log.error("[{}] 未知异常: error={}", operation, e.getMessage(), e);
            // 重新抛出BusinessException，确保事务回滚
            throw new BusinessException("系统异常，操作失败");
        }
    }
}