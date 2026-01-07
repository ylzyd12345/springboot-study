package com.kev1n.spring4demo.core.config;

import com.kev1n.spring4demo.api.dto.UserCreatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

/**
 * Spring Stream配置
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@Slf4j
public class StreamConfig {

    /**
     * 消息处理器
     * 用于处理用户消息的函数式接口
     */
    @Bean
    public Function<UserCreatedMessage, UserCreatedMessage> userProcessor() {
        return message -> {
            log.info("处理用户消息: {}", message);
            // 处理逻辑
            // TODO: 根据实际业务需求处理用户消息
            return message;
        };
    }
}