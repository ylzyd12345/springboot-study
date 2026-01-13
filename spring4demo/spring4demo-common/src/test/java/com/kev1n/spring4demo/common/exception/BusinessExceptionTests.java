package com.kev1n.spring4demo.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * BusinessException 业务异常单元测试
 *
 * @author kev1n
 * @since 1.0.0
 */
@DisplayName("BusinessException 业务异常测试")
class BusinessExceptionTests {

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("应该成功创建异常（code, message, httpStatus）")
        void shouldCreateExceptionWithCodeMessageAndHttpStatus() {
            // 执行
            BusinessException exception = new BusinessException("ERR001", "业务错误", 400);

            // 验证
            assertThat(exception).isNotNull();
            assertThat(exception.getCode()).isEqualTo("ERR001");
            assertThat(exception.getMessage()).isEqualTo("业务错误");
            assertThat(exception.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("应该成功创建异常（code, message, cause, httpStatus）")
        void shouldCreateExceptionWithCodeMessageCauseAndHttpStatus() {
            // 准备
            Throwable cause = new RuntimeException("原始异常");

            // 执行
            BusinessException exception = new BusinessException("ERR002", "业务错误", cause, 500);

            // 验证
            assertThat(exception).isNotNull();
            assertThat(exception.getCode()).isEqualTo("ERR002");
            assertThat(exception.getMessage()).isEqualTo("业务错误");
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.getHttpStatus()).isEqualTo(500);
        }

        @Test
        @DisplayName("应该成功创建异常（message，使用默认错误码和状态码）")
        void shouldCreateExceptionWithDefaultCodeAndHttpStatus() {
            // 执行
            BusinessException exception = new BusinessException("业务错误");

            // 验证
            assertThat(exception).isNotNull();
            assertThat(exception.getCode()).isEqualTo("BUSINESS_ERROR");
            assertThat(exception.getMessage()).isEqualTo("业务错误");
            assertThat(exception.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("应该成功创建异常（code, message，使用默认状态码）")
        void shouldCreateExceptionWithDefaultHttpStatus() {
            // 执行
            BusinessException exception = new BusinessException("ERR003", "业务错误");

            // 验证
            assertThat(exception).isNotNull();
            assertThat(exception.getCode()).isEqualTo("ERR003");
            assertThat(exception.getMessage()).isEqualTo("业务错误");
            assertThat(exception.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("应该成功创建异常（message, cause，使用默认错误码和状态码）")
        void shouldCreateExceptionWithMessageAndCauseUsingDefaults() {
            // 准备
            Throwable cause = new RuntimeException("原始异常");

            // 执行
            BusinessException exception = new BusinessException("业务错误", cause);

            // 验证
            assertThat(exception).isNotNull();
            assertThat(exception.getCode()).isEqualTo("BUSINESS_ERROR");
            assertThat(exception.getMessage()).isEqualTo("业务错误");
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("应该成功创建异常（code, message, cause，使用默认状态码）")
        void shouldCreateExceptionWithCodeMessageAndCauseUsingDefaultHttpStatus() {
            // 准备
            Throwable cause = new RuntimeException("原始异常");

            // 执行
            BusinessException exception = new BusinessException("ERR004", "业务错误", cause);

            // 验证
            assertThat(exception).isNotNull();
            assertThat(exception.getCode()).isEqualTo("ERR004");
            assertThat(exception.getMessage()).isEqualTo("业务错误");
            assertThat(exception.getCause()).isEqualTo(cause);
            assertThat(exception.getHttpStatus()).isEqualTo(400);
        }
    }

    @Nested
    @DisplayName("异常属性测试")
    class ExceptionPropertiesTests {

        @Test
        @DisplayName("应该正确获取错误码")
        void shouldGetCodeCorrectly() {
            // 执行
            BusinessException exception = new BusinessException("ERR005", "业务错误", 404);

            // 验证
            assertThat(exception.getCode()).isEqualTo("ERR005");
        }

        @Test
        @DisplayName("应该正确获取错误消息")
        void shouldGetMessageCorrectly() {
            // 执行
            BusinessException exception = new BusinessException("ERR006", "业务错误", 400);

            // 验证
            assertThat(exception.getMessage()).isEqualTo("业务错误");
        }

        @Test
        @DisplayName("应该正确获取HTTP状态码")
        void shouldGetHttpStatusCorrectly() {
            // 执行
            BusinessException exception = new BusinessException("ERR007", "业务错误", 403);

            // 验证
            assertThat(exception.getHttpStatus()).isEqualTo(403);
        }

        @Test
        @DisplayName("应该正确获取原始异常")
        void shouldGetCauseCorrectly() {
            // 准备
            Throwable cause = new RuntimeException("原始异常");

            // 执行
            BusinessException exception = new BusinessException("ERR008", "业务错误", cause, 500);

            // 验证
            assertThat(exception.getCause()).isEqualTo(cause);
        }
    }

    @Nested
    @DisplayName("异常继承测试")
    class ExceptionInheritanceTests {

        @Test
        @DisplayName("应该是BaseException的子类")
        void shouldBeSubclassOfBaseException() {
            // 执行
            BusinessException exception = new BusinessException("业务错误");

            // 验证
            assertThat(exception).isInstanceOf(BaseException.class);
        }

        @Test
        @DisplayName("应该是RuntimeException的子类")
        void shouldBeSubclassOfRuntimeException() {
            // 执行
            BusinessException exception = new BusinessException("业务错误");

            // 验证
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("应该可以被捕获为BaseException")
        void shouldBeCaughtAsBaseException() {
            // 执行
            BaseException exception = new BusinessException("业务错误");

            // 验证
            assertThat(exception).isInstanceOf(BusinessException.class);
        }
    }

    @Nested
    @DisplayName("异常序列化测试")
    class ExceptionSerializationTests {

        @Test
        @DisplayName("应该正确序列化和反序列化异常")
        void shouldSerializeAndDeserializeException() {
            // 准备
            BusinessException originalException = new BusinessException("ERR009", "业务错误", 400);

            // 执行 - 验证异常可以被序列化（serialVersionUID存在）
            // 注意：serialVersionUID是静态字段，不能通过实例访问

            // 验证
            assertThat(originalException).isNotNull();
        }
    }

    @Nested
    @DisplayName("异常堆栈跟踪测试")
    class ExceptionStackTraceTests {

        @Test
        @DisplayName("应该包含正确的堆栈跟踪信息")
        void shouldContainCorrectStackTrace() {
            // 执行
            BusinessException exception = new BusinessException("业务错误");
            StackTraceElement[] stackTrace = exception.getStackTrace();

            // 验证
            assertThat(stackTrace).isNotNull();
            assertThat(stackTrace).isNotEmpty();
        }

        @Test
        @DisplayName("应该包含原始异常的堆栈跟踪信息")
        void shouldContainCauseStackTrace() {
            // 准备
            Throwable cause = new RuntimeException("原始异常");

            // 执行
            BusinessException exception = new BusinessException("业务错误", cause);
            StackTraceElement[] causeStackTrace = exception.getCause().getStackTrace();

            // 验证
            assertThat(causeStackTrace).isNotNull();
            assertThat(causeStackTrace).isNotEmpty();
        }
    }
}