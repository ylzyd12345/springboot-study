#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从 spring4demo-core 模块迁移 document 和 repository 相关的类到 junmo-model 模块
"""

import os
import re

# 源模块路径
SOURCE_BASE = r"F:\codes\roadmap\github-project\springboot-study\spring4demo\spring4demo-core\src\main\java\com\kev1n\spring4demo\core"

# 目标模块路径
TARGET_BASE = r"F:\codes\roadmap\github-project\springboot-study\junmo-platform\junmo-model\src\main\java\com\junmo\platform\model"

# 需要迁移的文件清单（相对于 SOURCE_BASE）
FILES_TO_MIGRATE = [
    # document
    "document/UserDocument.java",
    "document/UserLog.java",

    # repository
    "repository/elasticsearch/UserDocumentRepository.java",
    "repository/mongo/UserLogRepository.java",
    "repository/neo4j/UserRepository.java",
    "repository/r2dbc/UserR2dbcRepository.java",
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
        'com.kev1n.spring4demo.core',
        'com.junmo.platform.model'
    )

    # 替换导入语句
    content = content.replace(
        'import com.kev1n.spring4demo.',
        'import com.junmo.platform.'
    )

    # 替换类引用
    content = re.sub(
        r'com\.kev1n\.spring4demo\.(core|common|api|base)\.',
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
    print("开始迁移 spring4demo-core 的 document 和 repository 到 junmo-model...")
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