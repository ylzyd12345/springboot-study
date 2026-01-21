#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从 junmo-platform-core 模块迁移业务逻辑相关的类到 junmo-core 模块
"""

import os
import re

# 源模块路径
SOURCE_BASE = r"F:\codes\roadmap\github-project\springboot-study\junmo-platform\junmo-platform-core\src\main\java\com\kev1n\junmo-platform\core"

# 目标模块路径
TARGET_BASE = r"F:\codes\roadmap\github-project\springboot-study\junmo-platform\junmo-core\src\main\java\com\junmo\platform\core"

# 需要迁移的文件清单（相对于 SOURCE_BASE）
FILES_TO_MIGRATE = [
    # annotation
    "annotation/RateLimit.java",
    "annotation/RateLimitException.java",

    # aspect
    "aspect/RateLimiterAspect.java",

    # config
    "config/AsyncConfig.java",
    "config/CacheConfig.java",
    "config/ElasticsearchConfig.java",
    "config/GraphQLConfig.java",
    "config/InfluxDBConfig.java",
    "config/KafkaConfig.java",
    "config/MongoConfig.java",
    "config/MybatisPlusConfig.java",
    "config/Neo4jConfig.java",
    "config/QuartzConfig.java",
    "config/RabbitMQConfig.java",
    "config/RateLimiterConfig.java",
    "config/SeataConfig.java",
    "config/WebSocketConfig.java",

    # graphql
    "graphql/UserMutationResolver.java",
    "graphql/UserQueryResolver.java",

    # handler
    "handler/JobExceptionHandler.java",
    "handler/MQExceptionHandler.java",

    # job
    "job/CacheRefreshJob.java",
    "job/QuartzJobDemo.java",
    "job/SpringTaskDemo.java",
    "job/UserCleanJob.java",
    "job/UserStatsJob.java",

    # mq
    "mq/KafkaMessageConsumer.java",
    "mq/MessageProducer.java",
    "mq/RabbitMQMessageConsumer.java",
    "mq/WebSocketEventListener.java",
    "mq/WebSocketMessageListener.java",

    # security
    "security/StpInterfaceImpl.java",
    "security/WebSocketHandshakeInterceptor.java",

    # service
    "service/AuthService.java",
    "service/DocumentPreviewService.java",
    "service/DynamicDataSourceService.java",
    "service/FileStorageService.java",
    "service/JobScheduleService.java",
    "service/RedissonServiceFacade.java",
    "service/SeataExampleService.java",
    "service/SeataService.java",
    "service/UserAsyncService.java",
    "service/UserCacheService.java",
    "service/UserDistributedService.java",
    "service/UserGraphService.java",
    "service/UserLogService.java",
    "service/UserMapperReactiveService.java",
    "service/UserMetricsService.java",
    "service/UserReactiveService.java",
    "service/UserSearchService.java",
    "service/UserService.java",
    "service/WebSocketService.java",

    # service/advanced
    "service/advanced/RedissonAdvancedDataService.java",
    "service/advanced/impl/RedissonAdvancedDataServiceImpl.java",

    # service/cache
    "service/cache/RedissonCacheService.java",
    "service/cache/impl/RedissonCacheServiceImpl.java",

    # service/collection
    "service/collection/RedissonCollectionService.java",
    "service/collection/impl/RedissonCollectionServiceImpl.java",

    # service/lock
    "service/lock/RedissonLockService.java",
    "service/lock/impl/RedissonLockServiceImpl.java",

    # service/impl
    "service/impl/DocumentPreviewServiceImpl.java",
    "service/impl/FileStorageServiceImpl.java",
    "service/impl/JobScheduleServiceImpl.java",
    "service/impl/UserLogServiceImpl.java",
    "service/impl/UserMapperReactiveServiceImpl.java",
    "service/impl/UserSearchServiceImpl.java",
    "service/impl/UserServiceImpl.java",
]


def migrate_file(relative_path):
    """迁移单个文件"""
    source_path = os.path.join(SOURCE_BASE, relative_path)
    target_path = os.path.join(TARGET_BASE, relative_path)

    # 确保目标目录存在
    target_dir = os.path.dirname(target_path)
    os.makedirs(target_dir, exist_ok=True)

    # 读取源文件
    with open(source_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # 替换包名
    content = content.replace(
        'com.junmo.junmo-platform.core',
        'com.junmo.platform.core'
    )

    # 替换导入语句
    content = content.replace(
        'import com.junmo.junmo-platform.',
        'import com.junmo.platform.'
    )

    # 替换类引用
    content = re.sub(
        r'com\.kev1n\.junmo-platform\.(core|common|api|base)\.',
        r'com.junmo.platform.\1.',
        content
    )

    # 替换作者信息
    content = re.sub(
        r'\* @author .*',
        '* @author junmo-platform',
        content
    )

    # 写入目标文件
    with open(target_path, 'w', encoding='utf-8') as f:
        f.write(content)

    print(f"✓ 迁移成功: {relative_path}")


def main():
    """主函数"""
    print("开始迁移 junmo-platform-core 到 junmo-core...")
    print(f"源路径: {SOURCE_BASE}")
    print(f"目标路径: {TARGET_BASE}")
    print(f"文件数量: {len(FILES_TO_MIGRATE)}")
    print("-" * 80)

    success_count = 0
    fail_count = 0

    for relative_path in FILES_TO_MIGRATE:
        try:
            migrate_file(relative_path)
            success_count += 1
        except Exception as e:
            print(f"✗ 迁移失败: {relative_path}")
            print(f"  错误: {e}")
            fail_count += 1

    print("-" * 80)
    print(f"迁移完成: 成功 {success_count} 个, 失败 {fail_count} 个")


if __name__ == "__main__":
    main()