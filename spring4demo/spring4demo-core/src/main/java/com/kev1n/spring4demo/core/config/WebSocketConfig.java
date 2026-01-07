package com.kev1n.spring4demo.core.config;

import com.kev1n.spring4demo.core.security.WebSocketHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket配置
 *
 * 功能说明:
 * 1. 配置WebSocket端点和STOMP协议
 * 2. 配置消息代理
 * 3. 配置拦截器实现用户认证和会话管理
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketHandshakeInterceptor handshakeInterceptor;

    /**
     * 注册STOMP端点
     *
     * @param registry STOMP端点注册器
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(handshakeInterceptor)
                .withSockJS();
    }

    /**
     * 配置消息代理
     *
     * @param registry 消息代理注册器
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用简单消息代理
        // /topic: 用于广播消息
        // /queue: 用于点对点消息
        registry.enableSimpleBroker("/topic", "/queue");

        // 设置应用目的地前缀
        // 客户端发送消息时需要加上此前缀
        registry.setApplicationDestinationPrefixes("/app");

        // 设置用户目的地前缀
        // 用于点对点消息发送
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * 配置客户端入站通道
     *
     * @param registration 通道注册器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 配置线程池
        registration.taskExecutor().corePoolSize(4).maxPoolSize(8);
    }

    /**
     * 配置客户端出站通道
     *
     * @param registration 通道注册器
     */
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        // 配置线程池
        registration.taskExecutor().corePoolSize(4).maxPoolSize(8);
    }
}