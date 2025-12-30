package com.kev1n.spring4demo.core.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 动态数据源示例服务
 *
 * <p>演示如何使用 @DS 注解切换数据源，实现读写分离和多数据源操作。</p>
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Slf4j
@Service
public class DynamicDataSourceService {

    /**
     * 使用主数据源（master）进行写操作
     *
     * <p>默认使用主数据源，也可以显式指定 @DS("master")</p>
     */
    @DS("master")
    public void writeToMaster() {
        log.info("正在使用 master 数据源执行写操作");
        // 这里可以执行数据库写操作
        // 例如：mapper.insert(entity);
    }

    /**
     * 使用从数据源（slave）进行读操作
     *
     * <p>使用 @DS("slave") 注解切换到从数据源</p>
     */
    @DS("slave")
    public List<Map<String, Object>> readFromSlave() {
        log.info("正在使用 slave 数据源执行读操作");
        // 这里可以执行数据库读操作
        // 例如：return mapper.selectList(null);
        return List.of();
    }

    /**
     * 使用默认数据源进行操作
     *
     * <p>不指定 @DS 注解时，使用配置文件中指定的 primary 数据源</p>
     */
    public void useDefaultDataSource() {
        log.info("正在使用默认数据源执行操作");
        // 这里可以执行数据库操作
    }

    /**
     * 演示读写分离
     *
     * <p>写操作使用 master，读操作使用 slave</p>
     */
    public void readWriteSeparation() {
        // 写操作 - 使用 master
        writeToMaster();

        // 读操作 - 使用 slave
        List<Map<String, Object>> data = readFromSlave();
        log.info("从 slave 数据源读取到 {} 条数据", data.size());
    }

    /**
     * 演示在同一个方法中切换数据源
     *
     * <p>注意：@DS 注解只对当前方法生效，不会影响其他方法</p>
     */
    @DS("master")
    public void switchDataSourceInMethod() {
        log.info("当前使用 master 数据源");

        // 调用其他方法时，会使用该方法上的 @DS 注解
        // 例如：readFromSlave() 会使用 slave 数据源
    }

    /**
     * 演示批量操作使用不同数据源
     *
     * <p>可以在不同的方法上使用不同的 @DS 注解来实现数据源切换</p>
     */
    public void batchOperationWithDifferentDataSource() {
        // 批量写入到 master
        for (int i = 0; i < 10; i++) {
            writeToMaster();
        }

        // 批量从 slave 读取
        for (int i = 0; i < 5; i++) {
            readFromSlave();
        }
    }

    /**
     * 演示事务中的数据源切换
     *
     * <p>注意：在事务中切换数据源时，需要谨慎处理</p>
     */
    @DS("master")
    public void transactionWithDataSourceSwitch() {
        log.info("在事务中使用 master 数据源");
        // 这里可以添加 @Transactional 注解
        // 注意：事务中的数据源切换可能会导致问题
        // 建议在事务开始前确定使用的数据源
    }
}