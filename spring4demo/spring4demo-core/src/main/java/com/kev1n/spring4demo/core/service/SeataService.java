package com.kev1n.spring4demo.core.service;

import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Seata 分布式事务服务封装类
 *
 * <p>提供常用的 Seata 分布式事务操作方法，包括 AT、TCC、SAGA、XA 等事务模式。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Service
public class SeataService {

    /**
     * 执行分布式事务（AT 模式）
     *
     * <p>AT 模式是 Seata 默认的事务模式，提供无侵入的分布式事务解决方案。</p>
     *
     * @param name 事务名称
     * @param timeout 超时时间（秒）
     * @param action 要执行的操作
     * @return 是否执行成功
     */
    @GlobalTransactional(name = "default-seata-transaction", timeoutMills = 60000, rollbackFor = Exception.class)
    public boolean executeInTransaction(String name, int timeout, Runnable action) {
        try {
            log.info("开始执行分布式事务，事务名称：{}，超时时间：{}秒", name, timeout);
            action.run();
            log.info("分布式事务执行成功，事务名称：{}", name);
            return true;
        } catch (Exception e) {
            log.error("分布式事务执行失败，事务名称：{}，错误信息：{}", name, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 执行分布式事务（AT 模式，有返回值）
     *
     * @param name 事务名称
     * @param timeout 超时时间（秒）
     * @param action 要执行的操作
     * @param <T> 返回值类型
     * @return 执行结果
     */
    @GlobalTransactional(name = "default-seata-transaction", timeoutMills = 60000, rollbackFor = Exception.class)
    public <T> T executeInTransactionWithResult(String name, int timeout, Supplier<T> action) throws Exception {
        try {
            log.info("开始执行分布式事务，事务名称：{}，超时时间：{}秒", name, timeout);
            T result = action.get();
            log.info("分布式事务执行成功，事务名称：{}，返回结果：{}", name, result);
            return result;
        } catch (Exception e) {
            log.error("分布式事务执行失败，事务名称：{}，错误信息：{}", name, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 执行分布式事务（默认配置）
     *
     * @param action 要执行的操作
     * @return 是否执行成功
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public boolean executeInTransaction(Runnable action) {
        return executeInTransaction("default-seata-transaction", 60, action);
    }

    /**
     * 执行分布式事务（默认配置，有返回值）
     *
     * @param action 要执行的操作
     * @param <T> 返回值类型
     * @return 执行结果
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public <T> T executeInTransactionWithResult(Supplier<T> action) throws Exception {
        return executeInTransactionWithResult("default-seata-transaction", 60, action);
    }

    /**
     * Supplier 接口（用于带返回值的操作）
     *
     * @param <T> 返回值类型
     */
    @FunctionalInterface
    public interface Supplier<T> {
        T get() throws Exception;
    }
}