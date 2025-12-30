package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.core.service.SeataExampleService;
import com.kev1n.spring4demo.core.service.SeataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Seata 分布式事务示例控制器
 *
 * <p>演示 Seata 分布式事务的使用，包括 AT 模式、事务回滚、超时等场景。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/seata")
@RequiredArgsConstructor
@Tag(name = "Seata 分布式事务示例", description = "演示 Seata 分布式事务的使用")
public class SeataController {

    private final SeataService seataService;
    private final SeataExampleService seataExampleService;

    /**
     * 基本的分布式事务示例
     *
     * @return 操作结果
     */
    @PostMapping("/basic")
    @Operation(summary = "基本的分布式事务示例", description = "演示基本的分布式事务使用")
    public Map<String, Object> basicTransaction() {
        Map<String, Object> result = new HashMap<>();

        try {
            seataExampleService.basicTransaction();

            result.put("status", "success");
            result.put("message", "分布式事务执行成功");

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "分布式事务执行失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 跨数据源的分布式事务示例
     *
     * @return 操作结果
     */
    @PostMapping("/datasource")
    @Operation(summary = "跨数据源的分布式事务示例", description = "演示跨数据源的分布式事务使用")
    public Map<String, Object> transactionWithDataSource() {
        Map<String, Object> result = new HashMap<>();

        try {
            seataExampleService.transactionWithDataSource();

            result.put("status", "success");
            result.put("message", "跨数据源分布式事务执行成功");

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "跨数据源分布式事务执行失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 事务回滚示例
     *
     * @return 操作结果
     */
    @PostMapping("/rollback")
    @Operation(summary = "事务回滚示例", description = "演示事务回滚场景")
    public Map<String, Object> transactionWithRollback() {
        Map<String, Object> result = new HashMap<>();

        try {
            seataExampleService.transactionWithRollback();

            result.put("status", "success");
            result.put("message", "分布式事务执行成功");

        } catch (Exception e) {
            result.put("status", "rollback");
            result.put("message", "分布式事务执行失败并回滚: " + e.getMessage());
            result.put("expected", "这是预期的回滚行为");
        }

        return result;
    }

    /**
     * 使用 SeataService 封装类示例
     *
     * @return 操作结果
     */
    @PostMapping("/seata-service")
    @Operation(summary = "使用 SeataService 封装类示例", description = "演示使用 SeataService 封装类简化分布式事务的使用")
    public Map<String, Object> transactionWithSeataService() {
        Map<String, Object> result = new HashMap<>();

        try {
            seataExampleService.transactionWithSeataService();

            result.put("status", "success");
            result.put("message", "使用 SeataService 的分布式事务执行成功");

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "分布式事务执行失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 嵌套事务示例
     *
     * @return 操作结果
     */
    @PostMapping("/nested")
    @Operation(summary = "嵌套事务示例", description = "演示嵌套分布式事务的使用")
    public Map<String, Object> nestedTransaction() {
        Map<String, Object> result = new HashMap<>();

        try {
            seataExampleService.nestedTransaction();

            result.put("status", "success");
            result.put("message", "嵌套分布式事务执行成功");

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "嵌套分布式事务执行失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 事务超时示例
     *
     * @return 操作结果
     */
    @PostMapping("/timeout")
    @Operation(summary = "事务超时示例", description = "演示事务超时场景")
    public Map<String, Object> transactionWithTimeout() {
        Map<String, Object> result = new HashMap<>();

        try {
            seataExampleService.transactionWithTimeout();

            result.put("status", "success");
            result.put("message", "分布式事务执行成功");

        } catch (Exception e) {
            result.put("status", "timeout");
            result.put("message", "分布式事务执行超时: " + e.getMessage());
            result.put("expected", "这是预期的超时行为");
        }

        return result;
    }

    /**
     * 自定义分布式事务示例
     *
     * @param name 事务名称
     * @param timeout 超时时间（秒）
     * @return 操作结果
     */
    @PostMapping("/custom")
    @Operation(summary = "自定义分布式事务示例", description = "演示自定义分布式事务的使用")
    public Map<String, Object> customTransaction(
            @RequestParam(defaultValue = "custom-seata-transaction") String name,
            @RequestParam(defaultValue = "60") int timeout) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = seataService.executeInTransaction(name, timeout, () -> {
                log.info("执行自定义分布式事务，名称：{}，超时：{}秒", name, timeout);
                // 执行业务操作
            });

            if (success) {
                result.put("status", "success");
                result.put("message", "自定义分布式事务执行成功");
                result.put("transactionName", name);
                result.put("timeout", timeout);
            } else {
                result.put("status", "failed");
                result.put("message", "自定义分布式事务执行失败");
            }

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "自定义分布式事务执行失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 查询事务状态示例
     *
     * @param xid 事务 XID
     * @return 操作结果
     */
    @GetMapping("/status/{xid}")
    @Operation(summary = "查询事务状态示例", description = "演示查询事务状态的使用")
    public Map<String, Object> queryTransactionStatus(@PathVariable String xid) {
        Map<String, Object> result = new HashMap<>();

        try {
            seataExampleService.queryTransactionStatus(xid);

            result.put("status", "success");
            result.put("message", "事务状态查询成功");
            result.put("xid", xid);

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "事务状态查询失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * Seata 健康检查
     *
     * @return 操作结果
     */
    @GetMapping("/health")
    @Operation(summary = "Seata 健康检查", description = "检查 Seata 服务是否正常")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 这里可以添加 Seata 健康检查逻辑
            // 例如：检查 Seata Server 连接状态

            result.put("status", "up");
            result.put("message", "Seata 服务正常");

        } catch (Exception e) {
            result.put("status", "down");
            result.put("message", "Seata 服务异常: " + e.getMessage());
        }

        return result;
    }
}