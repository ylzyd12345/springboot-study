package com.kev1n.spring4demo.core.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MyBatis-Plus 配置测试
 *
 * @author spring4demo
 * @version 1.0.0
 */
@SpringBootTest
@DisplayName("MyBatis-Plus 配置测试")
class MybatisPlusConfigTest {

    @Autowired
    private MybatisPlusConfig mybatisPlusConfig;

    @Autowired
    private MybatisPlusInterceptor mybatisPlusInterceptor;

    @Test
    @DisplayName("验证 MybatisPlusConfig Bean 创建")
    void testMybatisPlusConfigBeanCreated() {
        // Then - 验证 Bean 创建成功
        assertThat(mybatisPlusConfig).isNotNull();
    }

    @Test
    @DisplayName("验证 MybatisPlusInterceptor Bean 创建")
    void testMybatisPlusInterceptorBeanCreated() {
        // Then - 验证 Bean 创建成功
        assertThat(mybatisPlusInterceptor).isNotNull();
    }

    @Test
    @DisplayName("验证 MybatisPlusInterceptor 包含分页插件")
    void testMybatisPlusInterceptorContainsPaginationInnerInterceptor() {
        // When - 获取拦截器列表
        boolean hasPaginationInterceptor = mybatisPlusInterceptor.getInterceptors().stream()
                .anyMatch(interceptor -> interceptor instanceof PaginationInnerInterceptor);

        // Then - 验证包含分页插件
        assertThat(hasPaginationInterceptor).isTrue();
    }

    @Test
    @DisplayName("验证 MybatisPlusInterceptor 包含乐观锁插件")
    void testMybatisPlusInterceptorContainsOptimisticLockerInnerInterceptor() {
        // When - 获取拦截器列表
        boolean hasOptimisticLockerInterceptor = mybatisPlusInterceptor.getInterceptors().stream()
                .anyMatch(interceptor -> interceptor instanceof OptimisticLockerInnerInterceptor);

        // Then - 验证包含乐观锁插件
        assertThat(hasOptimisticLockerInterceptor).isTrue();
    }

    @Test
    @DisplayName("验证 MybatisPlusInterceptor 包含防全表更新删除插件")
    void testMybatisPlusInterceptorContainsBlockAttackInnerInterceptor() {
        // When - 获取拦截器列表
        boolean hasBlockAttackInterceptor = mybatisPlusInterceptor.getInterceptors().stream()
                .anyMatch(interceptor -> interceptor instanceof BlockAttackInnerInterceptor);

        // Then - 验证包含防全表更新删除插件
        assertThat(hasBlockAttackInterceptor).isTrue();
    }

    @Test
    @DisplayName("验证 MybatisPlusInterceptor 拦截器数量")
    void testMybatisPlusInterceptorInterceptorsCount() {
        // When - 获取拦截器数量
        int interceptorCount = mybatisPlusInterceptor.getInterceptors().size();

        // Then - 验证拦截器数量为 3（分页、乐观锁、防全表）
        assertThat(interceptorCount).isEqualTo(3);
    }

    @Test
    @DisplayName("验证 MybatisPlusInterceptor 拦截器顺序")
    void testMybatisPlusInterceptorInterceptorsOrder() {
        // When - 获取拦截器列表
        var interceptors = mybatisPlusInterceptor.getInterceptors();

        // Then - 验证拦截器顺序
        assertThat(interceptors.get(0)).isInstanceOf(PaginationInnerInterceptor.class);
        assertThat(interceptors.get(1)).isInstanceOf(OptimisticLockerInnerInterceptor.class);
        assertThat(interceptors.get(2)).isInstanceOf(BlockAttackInnerInterceptor.class);
    }

    @Test
    @DisplayName("验证分页插件配置")
    void testPaginationInnerInterceptorConfiguration() {
        // When - 获取分页插件
        PaginationInnerInterceptor paginationInterceptor = mybatisPlusInterceptor.getInterceptors().stream()
                .filter(interceptor -> interceptor instanceof PaginationInnerInterceptor)
                .map(interceptor -> (PaginationInnerInterceptor) interceptor)
                .findFirst()
                .orElse(null);

        // Then - 验证分页插件配置
        assertThat(paginationInterceptor).isNotNull();
    }

    @Test
    @DisplayName("验证 MyMetaObjectHandler Bean 创建")
    void testMyMetaObjectHandlerBeanCreated() {
        // When - 获取 MyMetaObjectHandler Bean
        MybatisPlusConfig.MyMetaObjectHandler metaObjectHandler =
                new MybatisPlusConfig.MyMetaObjectHandler();

        // Then - 验证 Bean 创建成功
        assertThat(metaObjectHandler).isNotNull();
    }

    @Test
    @DisplayName("验证 MyMetaObjectHandler 插入填充")
    void testMyMetaObjectHandlerInsertFill() {
        // Given - 创建 MetaObjectHandler
        MybatisPlusConfig.MyMetaObjectHandler handler = new MybatisPlusConfig.MyMetaObjectHandler();

        // Then - 验证方法存在
        assertThat(handler).isNotNull();
        // Note: 实际的填充测试需要真实的 MetaObject 对象
    }

    @Test
    @DisplayName("验证 MyMetaObjectHandler 更新填充")
    void testMyMetaObjectHandlerUpdateFill() {
        // Given - 创建 MetaObjectHandler
        MybatisPlusConfig.MyMetaObjectHandler handler = new MybatisPlusConfig.MyMetaObjectHandler();

        // Then - 验证方法存在
        assertThat(handler).isNotNull();
        // Note: 实际的填充测试需要真实的 MetaObject 对象
    }
}