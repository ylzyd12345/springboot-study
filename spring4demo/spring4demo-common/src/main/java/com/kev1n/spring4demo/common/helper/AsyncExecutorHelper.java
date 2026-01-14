package com.kev1n.spring4demo.common.helper;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 异步执行器辅助接口
 *
 * <p>统一处理异步操作异常，消除重复的异步操作异常处理代码。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
public interface AsyncExecutorHelper {

    /**
     * 执行异步操作并处理异常
     *
     * @param operation 操作名称
     * @param runnable 异步任务
     */
    void executeAsync(String operation, Runnable runnable);

    /**
     * 执行异步操作并处理异常（带参数）
     *
     * @param operation 操作名称
     * @param supplier 异步任务
     * @param <T> 返回类型
     */
    <T> void executeAsync(String operation, Supplier<T> supplier);

    /**
     * 执行异步操作并处理异常（带回调）
     *
     * @param operation 操作名称
     * @param runnable 异步任务
     * @param onSuccess 成功回调
     * @param onFailure 失败回调
     */
    void executeAsync(String operation, Runnable runnable,
                     Consumer<Void> onSuccess, Consumer<Exception> onFailure);

    /**
     * 执行异步操作并处理异常（带参数和回调）
     *
     * @param operation 操作名称
     * @param supplier 异步任务
     * @param onSuccess 成功回调
     * @param onFailure 失败回调
     * @param <T> 返回类型
     */
    <T> void executeAsync(String operation, Supplier<T> supplier,
                         Consumer<T> onSuccess, Consumer<Exception> onFailure);
}