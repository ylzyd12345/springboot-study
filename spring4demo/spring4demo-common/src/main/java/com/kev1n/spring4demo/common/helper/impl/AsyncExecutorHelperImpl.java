package com.kev1n.spring4demo.common.helper.impl;

import com.kev1n.spring4demo.common.exception.BusinessException;
import com.kev1n.spring4demo.common.helper.AsyncExecutorHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 异步执行器辅助实现类
 *
 * <p>统一处理异步操作异常，消除重复的异步操作异常处理代码。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Component
public class AsyncExecutorHelperImpl implements AsyncExecutorHelper {

    @Override
    public void executeAsync(String operation, Runnable runnable) {
        try {
            runnable.run();
            log.info("[{}] 操作执行成功", operation);
        } catch (BusinessException e) {
            log.error("[{}] 操作执行失败(业务异常): error={}", operation, e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("[{}] 操作执行失败(运行时异常): error={}", operation, e.getMessage(), e);
        } catch (Exception e) {
            log.error("[{}] 操作执行失败(未知异常)", operation, e);
        }
    }

    @Override
    public <T> void executeAsync(String operation, Supplier<T> supplier) {
        try {
            T result = supplier.get();
            log.info("[{}] 操作执行成功: result={}", operation, result);
        } catch (BusinessException e) {
            log.error("[{}] 操作执行失败(业务异常): error={}", operation, e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("[{}] 操作执行失败(运行时异常): error={}", operation, e.getMessage(), e);
        } catch (Exception e) {
            log.error("[{}] 操作执行失败(未知异常)", operation, e);
        }
    }

    @Override
    public void executeAsync(String operation, Runnable runnable,
                           Consumer<Void> onSuccess, Consumer<Exception> onFailure) {
        try {
            runnable.run();
            log.info("[{}] 操作执行成功", operation);
            if (onSuccess != null) {
                onSuccess.accept(null);
            }
        } catch (BusinessException e) {
            log.error("[{}] 操作执行失败(业务异常): error={}", operation, e.getMessage(), e);
            if (onFailure != null) {
                onFailure.accept(e);
            }
        } catch (RuntimeException e) {
            log.error("[{}] 操作执行失败(运行时异常): error={}", operation, e.getMessage(), e);
            if (onFailure != null) {
                onFailure.accept(e);
            }
        } catch (Exception e) {
            log.error("[{}] 操作执行失败(未知异常)", operation, e);
            if (onFailure != null) {
                onFailure.accept(e);
            }
        }
    }

    @Override
    public <T> void executeAsync(String operation, Supplier<T> supplier,
                                Consumer<T> onSuccess, Consumer<Exception> onFailure) {
        try {
            T result = supplier.get();
            log.info("[{}] 操作执行成功: result={}", operation, result);
            if (onSuccess != null) {
                onSuccess.accept(result);
            }
        } catch (BusinessException e) {
            log.error("[{}] 操作执行失败(业务异常): error={}", operation, e.getMessage(), e);
            if (onFailure != null) {
                onFailure.accept(e);
            }
        } catch (RuntimeException e) {
            log.error("[{}] 操作执行失败(运行时异常): error={}", operation, e.getMessage(), e);
            if (onFailure != null) {
                onFailure.accept(e);
            }
        } catch (Exception e) {
            log.error("[{}] 操作执行失败(未知异常)", operation, e);
            if (onFailure != null) {
                onFailure.accept(e);
            }
        }
    }
}