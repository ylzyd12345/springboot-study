#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
修复 junmo-core 模块中的导入语句，将 entity/mapper/document/repository 的导入从 core 改为 model
"""

import os
import re

# 目标模块路径
TARGET_BASE = r"F:\codes\roadmap\github-project\springboot-study\junmo-platform\junmo-core\src\main\java\com\junmo\platform\core"

# 导入映射表
IMPORT_MAPPINGS = {
    # entity
    r'import com\.junmo\.platform\.core\.entity\.User;': 'import com.junmo.platform.model.entity.User;',
    r'import com\.junmo\.platform\.core\.entity\.neo4j\.UserNode;': 'import com.junmo.platform.model.entity.neo4j.UserNode;',

    # mapper
    r'import com\.junmo\.platform\.core\.mapper\.UserMapper;': 'import com.junmo.platform.model.mapper.UserMapper;',

    # document
    r'import com\.junmo\.platform\.core\.document\.UserDocument;': 'import com.junmo.platform.model.document.UserDocument;',
    r'import com\.junmo\.platform\.core\.document\.UserLog;': 'import com.junmo.platform.model.document.UserLog;',

    # repository
    r'import com\.junmo\.platform\.core\.repository\.elasticsearch\.UserDocumentRepository;': 'import com.junmo.platform.model.repository.elasticsearch.UserDocumentRepository;',
    r'import com\.junmo\.platform\.core\.repository\.mongo\.UserLogRepository;': 'import com.junmo.platform.model.repository.mongo.UserLogRepository;',
    r'import com\.junmo\.platform\.core\.repository\.neo4j\.UserRepository;': 'import com.junmo.platform.model.repository.neo4j.UserRepository;',
    r'import com\.junmo\.platform\.core\.repository\.r2dbc\.UserR2dbcRepository;': 'import com.junmo.platform.model.repository.r2dbc.UserR2dbcRepository;',
}


def fix_file_imports(file_path):
    """修复单个文件的导入语句"""
    # 读取文件
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    original_content = content

    # 替换所有导入语句
    for old_import, new_import in IMPORT_MAPPINGS.items():
        content = re.sub(old_import, new_import, content)

    # 如果内容有变化，写回文件
    if content != original_content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False


def main():
    """主函数"""
    print("开始修复 junmo-core 模块中的导入语句...")
    print(f"目标路径: {TARGET_BASE}")
    print("-" * 80)

    modified_count = 0

    # 遍历所有 Java 文件
    for root, dirs, files in os.walk(TARGET_BASE):
        for file in files:
            if file.endswith('.java'):
                file_path = os.path.join(root, file)
                relative_path = os.path.relpath(file_path, TARGET_BASE)

                try:
                    if fix_file_imports(file_path):
                        print(f"✓ 修复成功: {relative_path}")
                        modified_count += 1
                except Exception as e:
                    print(f"✗ 修复失败: {relative_path}")
                    print(f"  错误: {e}")

    print("-" * 80)
    print(f"修复完成: 修改了 {modified_count} 个文件")


if __name__ == "__main__":
    main()