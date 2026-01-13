package com.kev1n.spring4demo.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * BeanCopyUtil 工具类单元测试
 *
 * @author kev1n
 * @since 1.0.0
 */
@DisplayName("BeanCopyUtil 工具类测试")
class BeanCopyUtilTests {

    /**
     * 测试用的源对象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class SourceDto {
        private Long id;
        private String name;
        private Integer age;
        private String email;
    }

    /**
     * 测试用的目标对象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class TargetDto {
        private Long id;
        private String name;
        private Integer age;
        private String email;
        private String extraField;
    }

    private SourceDto sourceDto;
    private TargetDto targetDto;

    @BeforeEach
    void setUp() {
        sourceDto = new SourceDto(1L, "张三", 25, "zhangsan@example.com");
        targetDto = new TargetDto();
    }

    @Nested
    @DisplayName("copyProperties 方法测试")
    class CopyPropertiesTests {

        @Test
        @DisplayName("应该成功拷贝对象属性")
        void shouldCopyPropertiesSuccessfully() {
            // 执行
            BeanCopyUtil.copyProperties(sourceDto, targetDto);

            // 验证
            assertThat(targetDto.getId()).isEqualTo(sourceDto.getId());
            assertThat(targetDto.getName()).isEqualTo(sourceDto.getName());
            assertThat(targetDto.getAge()).isEqualTo(sourceDto.getAge());
            assertThat(targetDto.getEmail()).isEqualTo(sourceDto.getEmail());
        }

        @Test
        @DisplayName("应该成功拷贝对象属性并忽略指定属性")
        void shouldCopyPropertiesWithIgnoreFields() {
            // 执行
            BeanCopyUtil.copyProperties(sourceDto, targetDto, "email");

            // 验证
            assertThat(targetDto.getId()).isEqualTo(sourceDto.getId());
            assertThat(targetDto.getName()).isEqualTo(sourceDto.getName());
            assertThat(targetDto.getAge()).isEqualTo(sourceDto.getAge());
            assertThat(targetDto.getEmail()).isNull();
        }

        @Test
        @DisplayName("当源对象为null时应该返回目标对象")
        void shouldReturnTargetWhenSourceIsNull() {
            // 执行
            BeanCopyUtil.copyProperties(null, targetDto);

            // 验证
            assertThat(targetDto.getId()).isNull();
            assertThat(targetDto.getName()).isNull();
        }

        @Test
        @DisplayName("当目标对象为null时应该返回null")
        void shouldReturnNullWhenTargetIsNull() {
            // 执行
            TargetDto result = BeanCopyUtil.copyProperties(sourceDto, (TargetDto) null);

            // 验证
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("copyPropertiesWithCopier 方法测试")
    class CopyPropertiesWithCopierTests {

        @Test
        @DisplayName("应该使用BeanCopier成功拷贝对象属性")
        void shouldCopyPropertiesWithCopierSuccessfully() {
            // 执行
            BeanCopyUtil.copyPropertiesWithCopier(sourceDto, targetDto);

            // 验证
            assertThat(targetDto.getId()).isEqualTo(sourceDto.getId());
            assertThat(targetDto.getName()).isEqualTo(sourceDto.getName());
            assertThat(targetDto.getAge()).isEqualTo(sourceDto.getAge());
            assertThat(targetDto.getEmail()).isEqualTo(sourceDto.getEmail());
        }

        @Test
        @DisplayName("当源对象为null时应该返回目标对象")
        void shouldReturnTargetWhenSourceIsNullWithCopier() {
            // 执行
            BeanCopyUtil.copyPropertiesWithCopier(null, targetDto);

            // 验证
            assertThat(targetDto.getId()).isNull();
            assertThat(targetDto.getName()).isNull();
        }

        @Test
        @DisplayName("当目标对象为null时应该返回null")
        void shouldReturnNullWhenTargetIsNullWithCopier() {
            // 执行
            TargetDto result = BeanCopyUtil.copyPropertiesWithCopier(sourceDto, (TargetDto) null);

            // 验证
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("copyProperties(Class<T>) 方法测试")
    class CopyPropertiesWithClassTests {

        @Test
        @DisplayName("应该成功拷贝对象属性并返回新对象")
        void shouldCopyPropertiesAndReturnNewObject() {
            // 执行
            TargetDto result = BeanCopyUtil.copyProperties(sourceDto, TargetDto.class);

            // 验证
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(sourceDto.getId());
            assertThat(result.getName()).isEqualTo(sourceDto.getName());
            assertThat(result.getAge()).isEqualTo(sourceDto.getAge());
            assertThat(result.getEmail()).isEqualTo(sourceDto.getEmail());
        }

        @Test
        @DisplayName("当源对象为null时应该返回null")
        void shouldReturnNullWhenSourceIsNullWithClass() {
            // 执行
            TargetDto result = BeanCopyUtil.copyProperties(null, TargetDto.class);

            // 验证
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("copyPropertiesWithCopier(Class<T>) 方法测试")
    class CopyPropertiesWithCopierAndClassTests {

        @Test
        @DisplayName("应该使用BeanCopier成功拷贝对象属性并返回新对象")
        void shouldCopyPropertiesWithCopierAndReturnNewObject() {
            // 执行
            TargetDto result = BeanCopyUtil.copyPropertiesWithCopier(sourceDto, TargetDto.class);

            // 验证
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(sourceDto.getId());
            assertThat(result.getName()).isEqualTo(sourceDto.getName());
            assertThat(result.getAge()).isEqualTo(sourceDto.getAge());
            assertThat(result.getEmail()).isEqualTo(sourceDto.getEmail());
        }

        @Test
        @DisplayName("当源对象为null时应该返回null")
        void shouldReturnNullWhenSourceIsNullWithCopierAndClass() {
            // 执行
            TargetDto result = BeanCopyUtil.copyPropertiesWithCopier(null, TargetDto.class);

            // 验证
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("copyList 方法测试")
    class CopyListTests {

        @Test
        @DisplayName("应该成功拷贝列表对象")
        void shouldCopyListSuccessfully() {
            // 准备
            List<SourceDto> sourceList = Arrays.asList(
                    new SourceDto(1L, "张三", 25, "zhangsan@example.com"),
                    new SourceDto(2L, "李四", 30, "lisi@example.com"),
                    new SourceDto(3L, "王五", 28, "wangwu@example.com")
            );

            // 执行
            List<TargetDto> result = BeanCopyUtil.copyList(sourceList, TargetDto.class);

            // 验证
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getName()).isEqualTo("张三");
            assertThat(result.get(1).getId()).isEqualTo(2L);
            assertThat(result.get(1).getName()).isEqualTo("李四");
            assertThat(result.get(2).getId()).isEqualTo(3L);
            assertThat(result.get(2).getName()).isEqualTo("王五");
        }

        @Test
        @DisplayName("当源列表为null时应该返回空列表")
        void shouldReturnEmptyListWhenSourceListIsNull() {
            // 执行
            List<TargetDto> result = BeanCopyUtil.copyList(null, TargetDto.class);

            // 验证
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("当源列表为空时应该返回空列表")
        void shouldReturnEmptyListWhenSourceListIsEmpty() {
            // 执行
            List<TargetDto> result = BeanCopyUtil.copyList(Collections.emptyList(), TargetDto.class);

            // 验证
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("copyListWithCopier 方法测试")
    class CopyListWithCopierTests {

        @Test
        @DisplayName("应该使用BeanCopier成功拷贝列表对象")
        void shouldCopyListWithCopierSuccessfully() {
            // 准备
            List<SourceDto> sourceList = Arrays.asList(
                    new SourceDto(1L, "张三", 25, "zhangsan@example.com"),
                    new SourceDto(2L, "李四", 30, "lisi@example.com")
            );

            // 执行
            List<TargetDto> result = BeanCopyUtil.copyListWithCopier(sourceList, TargetDto.class);

            // 验证
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getName()).isEqualTo("张三");
            assertThat(result.get(1).getId()).isEqualTo(2L);
            assertThat(result.get(1).getName()).isEqualTo("李四");
        }

        @Test
        @DisplayName("当源列表为null时应该返回空列表")
        void shouldReturnEmptyListWhenSourceListIsNullWithCopier() {
            // 执行
            List<TargetDto> result = BeanCopyUtil.copyListWithCopier(null, TargetDto.class);

            // 验证
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("缓存管理方法测试")
    class CacheManagementTests {

        @Test
        @DisplayName("应该成功清空BeanCopier缓存")
        void shouldClearCacheSuccessfully() {
            // 准备 - 先执行一些拷贝操作以填充缓存
            BeanCopyUtil.copyPropertiesWithCopier(sourceDto, targetDto);
            int cacheSizeBefore = BeanCopyUtil.getCacheSize();

            // 执行
            BeanCopyUtil.clearCache();

            // 验证
            int cacheSizeAfter = BeanCopyUtil.getCacheSize();
            assertThat(cacheSizeBefore).isGreaterThan(0);
            assertThat(cacheSizeAfter).isEqualTo(0);
        }

        @Test
        @DisplayName("应该正确获取缓存大小")
        void shouldGetCacheSizeCorrectly() {
            // 准备
            BeanCopyUtil.clearCache();

            // 执行
            int cacheSizeBefore = BeanCopyUtil.getCacheSize();
            BeanCopyUtil.copyPropertiesWithCopier(sourceDto, targetDto);
            int cacheSizeAfter = BeanCopyUtil.getCacheSize();

            // 验证
            assertThat(cacheSizeBefore).isEqualTo(0);
            assertThat(cacheSizeAfter).isEqualTo(1);
        }
    }
}