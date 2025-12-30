package com.kev1n.spring4demo.web.controller;

import com.kev1n.spring4demo.core.service.DynamicDataSourceService;
import com.kev1n.spring4demo.core.service.RedissonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redisson 和动态数据源示例控制器
 *
 * <p>演示 Redisson 分布式锁、缓存等功能，以及动态数据源的读写分离。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
@Tag(name = "Redisson和动态数据源示例", description = "演示 Redisson 分布式锁、缓存和动态数据源读写分离")
public class RedissonAndDataSourceController {

    private final RedissonService redissonService;
    private final DynamicDataSourceService dynamicDataSourceService;

    /**
     * Redisson 分布式锁示例
     *
     * <p>演示如何使用 Redisson 实现分布式锁</p>
     *
     * @param lockKey 锁的 key
     * @return 操作结果
     */
    @GetMapping("/redisson/lock")
    @Operation(summary = "Redisson 分布式锁示例", description = "演示如何使用 Redisson 实现分布式锁")
    public Map<String, Object> redissonLock(@RequestParam(defaultValue = "test_lock") String lockKey) {
        Map<String, Object> result = new HashMap<>();

        // 尝试获取锁
        boolean locked = redissonService.tryLock(lockKey, 10, 30, TimeUnit.SECONDS);

        if (locked) {
            try {
                result.put("status", "success");
                result.put("message", "获取锁成功");
                result.put("lockKey", lockKey);

                // 模拟业务操作
                log.info("执行业务操作，锁 key: {}", lockKey);
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                result.put("status", "error");
                result.put("message", "操作被中断");
            } finally {
                // 释放锁
                redissonService.unlock(lockKey);
                result.put("unlock", "锁已释放");
            }
        } else {
            result.put("status", "failed");
            result.put("message", "获取锁失败");
            result.put("lockKey", lockKey);
        }

        return result;
    }

    /**
     * Redisson 缓存示例
     *
     * <p>演示如何使用 Redisson 进行缓存操作</p>
     *
     * @param key 缓存 key
     * @param value 缓存值
     * @return 操作结果
     */
    @PostMapping("/redisson/cache")
    @Operation(summary = "Redisson 缓存示例", description = "演示如何使用 Redisson 进行缓存操作")
    public Map<String, Object> redissonCache(@RequestParam String key, @RequestParam String value) {
        Map<String, Object> result = new HashMap<>();

        // 设置缓存
        redissonService.set(key, value, 60, TimeUnit.SECONDS);

        result.put("status", "success");
        result.put("message", "缓存设置成功");
        result.put("key", key);
        result.put("value", value);
        result.put("expireTime", "60s");

        return result;
    }

    /**
     * Redisson 获取缓存示例
     *
     * <p>演示如何从 Redisson 获取缓存</p>
     *
     * @param key 缓存 key
     * @return 操作结果
     */
    @GetMapping("/redisson/cache")
    @Operation(summary = "Redisson 获取缓存示例", description = "演示如何从 Redisson 获取缓存")
    public Map<String, Object> getRedissonCache(@RequestParam String key) {
        Map<String, Object> result = new HashMap<>();

        // 获取缓存
        String value = redissonService.get(key);

        if (value != null) {
            result.put("status", "success");
            result.put("message", "缓存获取成功");
            result.put("key", key);
            result.put("value", value);
            result.put("exists", true);
        } else {
            result.put("status", "not_found");
            result.put("message", "缓存不存在");
            result.put("key", key);
            result.put("exists", false);
        }

        return result;
    }

    /**
     * 动态数据源 - 写入主库示例
     *
     * <p>演示如何使用 @DS 注解切换到主数据源进行写操作</p>
     *
     * @return 操作结果
     */
    @PostMapping("/datasource/write")
    @Operation(summary = "动态数据源 - 写入主库示例", description = "演示如何使用 @DS 注解切换到主数据源进行写操作")
    public Map<String, Object> writeToMaster() {
        Map<String, Object> result = new HashMap<>();

        try {
            dynamicDataSourceService.writeToMaster();

            result.put("status", "success");
            result.put("message", "数据已写入主库 (master)");
            result.put("datasource", "master");

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "写入失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 动态数据源 - 从从库读取示例
     *
     * <p>演示如何使用 @DS 注解切换到从数据源进行读操作</p>
     *
     * @return 操作结果
     */
    @GetMapping("/datasource/read")
    @Operation(summary = "动态数据源 - 从从库读取示例", description = "演示如何使用 @DS 注解切换到从数据源进行读操作")
    public Map<String, Object> readFromSlave() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Map<String, Object>> data = dynamicDataSourceService.readFromSlave();

            result.put("status", "success");
            result.put("message", "数据已从从库 (slave) 读取");
            result.put("datasource", "slave");
            result.put("dataCount", data.size());
            result.put("data", data);

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "读取失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 动态数据源 - 读写分离示例
     *
     * <p>演示如何实现读写分离，写操作使用主库，读操作使用从库</p>
     *
     * @return 操作结果
     */
    @PostMapping("/datasource/readwrite")
    @Operation(summary = "动态数据源 - 读写分离示例", description = "演示如何实现读写分离，写操作使用主库，读操作使用从库")
    public Map<String, Object> readWriteSeparation() {
        Map<String, Object> result = new HashMap<>();

        try {
            dynamicDataSourceService.readWriteSeparation();

            result.put("status", "success");
            result.put("message", "读写分离操作完成");
            result.put("writeDatasource", "master");
            result.put("readDatasource", "slave");

        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "操作失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * Redisson Map 操作示例
     *
     * <p>演示如何使用 Redisson 的 Map 数据结构</p>
     *
     * @param key Map 的 key
     * @param mapKey Map 中的 key
     * @param value Map 中的 value
     * @return 操作结果
     */
    @PostMapping("/redisson/map")
    @Operation(summary = "Redisson Map 操作示例", description = "演示如何使用 Redisson 的 Map 数据结构")
    public Map<String, Object> redissonMap(
            @RequestParam String key,
            @RequestParam String mapKey,
            @RequestParam String value) {
        Map<String, Object> result = new HashMap<>();

        // 设置 Map 值
        redissonService.mapPut(key, mapKey, value);

        // 获取 Map 值
        String retrievedValue = redissonService.mapGet(key, mapKey);

        result.put("status", "success");
        result.put("message", "Map 操作成功");
        result.put("key", key);
        result.put("mapKey", mapKey);
        result.put("value", value);
        result.put("retrievedValue", retrievedValue);

        return result;
    }

    /**
     * Redisson Set 操作示例
     *
     * <p>演示如何使用 Redisson 的 Set 数据结构</p>
     *
     * @param key Set 的 key
     * @param value Set 中的 value
     * @return 操作结果
     */
    @PostMapping("/redisson/set")
    @Operation(summary = "Redisson Set 操作示例", description = "演示如何使用 Redisson 的 Set 数据结构")
    public Map<String, Object> redissonSet(@RequestParam String key, @RequestParam String value) {
        Map<String, Object> result = new HashMap<>();

        // 添加到 Set
        boolean added = redissonService.setAdd(key, value);

        result.put("status", "success");
        result.put("message", "Set 操作成功");
        result.put("key", key);
        result.put("value", value);
        result.put("added", added);

        return result;
    }

    /**
     * Redisson List 操作示例
     *
     * <p>演示如何使用 Redisson 的 List 数据结构</p>
     *
     * @param key List 的 key
     * @param value List 中的 value
     * @return 操作结果
     */
    @PostMapping("/redisson/list")
    @Operation(summary = "Redisson List 操作示例", description = "演示如何使用 Redisson 的 List 数据结构")
    public Map<String, Object> redissonList(@RequestParam String key, @RequestParam String value) {
        Map<String, Object> result = new HashMap<>();

        // 添加到 List
        boolean added = redissonService.listAdd(key, value);

        // 获取 List 中的第一个元素
        String firstValue = redissonService.listGet(key, 0);

        result.put("status", "success");
        result.put("message", "List 操作成功");
        result.put("key", key);
        result.put("value", value);
        result.put("added", added);
        result.put("firstValue", firstValue);

        return result;
    }
}