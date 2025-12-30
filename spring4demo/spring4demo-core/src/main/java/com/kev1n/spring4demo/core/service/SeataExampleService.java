package com.kev1n.spring4demo.core.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Seata 分布式事务示例服务
 *
 * <p>演示如何使用 Seata 实现分布式事务，包括 AT 模式的使用。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Service
public class SeataExampleService {

    private final SeataService seataService;

    public SeataExampleService(SeataService seataService) {
        this.seataService = seataService;
    }

    /**
     * 演示基本的分布式事务
     *
     * <p>使用 @GlobalTransactional 注解标记方法，实现分布式事务。</p>
     */
    @GlobalTransactional(name = "basic-seata-transaction", timeoutMills = 60000, rollbackFor = Exception.class)
    public void basicTransaction() {
        log.info("开始执行基本的分布式事务");

        // 模拟业务操作
        // 例如：调用多个服务的数据库操作
        // userService.createUser(user);
        // orderService.createOrder(order);

        // 如果任何操作失败，整个事务会回滚
        log.info("基本的分布式事务执行成功");
    }

    /**
     * 演示带数据源的分布式事务
     *
     * <p>结合动态数据源和 Seata 实现跨数据源的分布式事务。</p>
     */
    @GlobalTransactional(name = "datasource-seata-transaction", timeoutMills = 60000, rollbackFor = Exception.class)
    public void transactionWithDataSource() {
        log.info("开始执行跨数据源的分布式事务");

        // 使用主数据源进行写操作
        writeToMaster();

        // 使用从数据源进行读操作
        readFromSlave();

        // 如果任何操作失败，整个事务会回滚
        log.info("跨数据源的分布式事务执行成功");
    }

    /**
     * 演示事务回滚
     *
     * <p>当抛出异常时，Seata 会自动回滚所有已执行的操作。</p>
     */
    @GlobalTransactional(name = "rollback-seata-transaction", timeoutMills = 60000, rollbackFor = Exception.class)
    public void transactionWithRollback() {
        log.info("开始执行会回滚的分布式事务");

        try {
            // 执行一些操作
            // userService.createUser(user);

            // 模拟业务异常
            if (true) {
                throw new RuntimeException("模拟业务异常，触发事务回滚");
            }

            log.info("分布式事务执行成功");
        } catch (Exception e) {
            log.error("分布式事务执行失败，触发回滚", e);
            throw e;
        }
    }

    /**
     * 演示使用 SeataService 封装类
     *
     * <p>使用 SeataService 封装类简化分布式事务的使用。</p>
     */
    public void transactionWithSeataService() {
        seataService.executeInTransaction("seata-service-transaction", 60, () -> {
            log.info("使用 SeataService 执行分布式事务");

            // 执行业务操作
            // userService.createUser(user);
            // orderService.createOrder(order);

            log.info("分布式事务执行成功");
        });
    }

    /**
     * 演示嵌套事务
     *
     * <p>Seata 支持嵌套事务，但需要注意事务传播行为。</p>
     */
    @GlobalTransactional(name = "nested-seata-transaction", timeoutMills = 60000, rollbackFor = Exception.class)
    public void nestedTransaction() {
        log.info("开始执行嵌套分布式事务");

        // 执行第一个操作
        // userService.createUser(user);

        // 调用另一个带有 @GlobalTransactional 的方法
        // 注意：嵌套的全局事务会被合并为一个事务
        basicTransaction();

        log.info("嵌套分布式事务执行成功");
    }

    /**
     * 演示事务超时
     *
     * <p>当事务执行时间超过配置的超时时间时，Seata 会自动回滚事务。</p>
     */
    @GlobalTransactional(name = "timeout-seata-transaction", timeoutMills = 3000, rollbackFor = Exception.class)
    public void transactionWithTimeout() {
        log.info("开始执行会超时的分布式事务");

        try {
            // 模拟长时间操作
            Thread.sleep(5000);

            log.info("分布式事务执行成功");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("分布式事务被中断", e);
            throw new RuntimeException("事务被中断");
        }
    }

    /**
     * 演示事务状态查询
     *
     * <p>可以通过 Seata 的 API 查询事务状态。</p>
     */
    public void queryTransactionStatus(String xid) {
        log.info("查询事务状态，XID：{}", xid);

        // 可以通过 Seata 的 API 查询事务状态
        // GlobalTransactionContext.reload(xid).getStatus();

        log.info("事务状态查询完成");
    }

    /**
     * 使用主数据源进行写操作
     */
    @DS("master")
    private void writeToMaster() {
        log.info("使用 master 数据源进行写操作");
        // 这里可以执行数据库写操作
        // 例如：mapper.insert(entity);
    }

    /**
     * 使用从数据源进行读操作
     */
    @DS("slave")
    private void readFromSlave() {
        log.info("使用 slave 数据源进行读操作");
        // 这里可以执行数据库读操作
        // 例如：mapper.selectList(null);
    }
}